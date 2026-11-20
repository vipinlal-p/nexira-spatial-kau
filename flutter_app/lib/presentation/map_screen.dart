import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_map/flutter_map.dart';
import 'package:latlong2/latlong.dart';
import 'package:nexira_spatial/core/api_client.dart';
import 'package:nexira_spatial/core/auth_provider.dart';
import 'package:nexira_spatial/data/models.dart';

class MapScreen extends ConsumerStatefulWidget {
  const MapScreen({super.key});

  @override
  ConsumerState<MapScreen> createState() => _MapScreenState();
}

class _MapScreenState extends ConsumerState<MapScreen> {
  final MapController _mapController = MapController();
  List<PaddyFieldFeature> _fields = [];
  bool _isLoading = true;
  PaddyFieldFeature? _selectedField;

  @override
  void initState() {
    super.initState();
    _fetchFields();
  }

  Future<void> _fetchFields() async {
    setState(() => _isLoading = true);
    try {
      final response = await ApiClient().dio.get('/fields');
      final data = response.data;
      final featuresList = data['features'] as List? ?? [];
      
      setState(() {
        _fields = featuresList.map((f) => PaddyFieldFeature.fromJson(f)).toList();
        _isLoading = false;
      });
    } catch (e) {
      setState(() => _isLoading = false);
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Failed to load GIS fields: $e')),
        );
      }
    }
  }

  void _showFieldDetails(PaddyFieldFeature field) {
    setState(() => _selectedField = field);
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(16)),
      ),
      builder: (context) => Padding(
        padding: const EdgeInsets.all(20.0),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.between,
              children: [
                Text(
                  'Survey No: ${field.surveyNumber}',
                  style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: Color(0xFF1B5E20)),
                ),
                IconButton(
                  icon: const Icon(Icons.close),
                  onPressed: () => Navigator.pop(context),
                ),
              ],
            ),
            const Divider(),
            ListTile(
              leading: const Icon(Icons.person, color: Colors.green),
              title: Text('Farmer: ${field.farmerName}'),
              subtitle: Text('Phone: ${field.farmerPhone}'),
            ),
            ListTile(
              leading: const Icon(Icons.landscape, color: Colors.green),
              title: Text('Area: ${field.areaHa} Hectares'),
              subtitle: Text('Season: ${field.cropSeason} | Soil: ${field.soilType}'),
            ),
            const SizedBox(height: 8),
            const Text('GIS Attributes:', style: TextStyle(fontWeight: FontWeight.bold)),
            const SizedBox(height: 4),
            Container(
              padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(
                color: Colors.grey[100],
                borderRadius: BorderRadius.circular(8),
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: field.attributes.entries.map((e) => Text('${e.key}: ${e.value}')).toList(),
              ),
            ),
            const SizedBox(height: 16),
            SizedBox(
              width: double.infinity,
              child: ElevatedButton.icon(
                style: ElevatedButton.styleFrom(backgroundColor: const Color(0xFF2E7D32), foregroundColor: Colors.white),
                icon: const Icon(Icons.my_location),
                label: const Text('Zoom To Feature'),
                onPressed: () {
                  Navigator.pop(context);
                  if (field.polygonCoordinates.isNotEmpty) {
                    _mapController.move(field.polygonCoordinates[0], 16.0);
                  }
                },
              ),
            ),
          ],
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final authState = ref.watch(authProvider);
    final currentUser = authState.user;

    // Center map around Thrissur / Mannuthy area
    final initialCenter = const LatLng(10.547, 10.547).latitude == 10.547
        ? const LatLng(10.547, 76.290)
        : const LatLng(10.547, 76.290);

    return Scaffold(
      appBar: AppBar(
        title: const Text('KAU Paddy Field GIS Map'),
        backgroundColor: const Color(0xFF1B5E20),
        foregroundColor: Colors.white,
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: _fetchFields,
            tooltip: 'Refresh GIS Data',
          ),
        ],
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : FlutterMap(
              mapController: _mapController,
              options: MapOptions(
                initialCenter: initialCenter,
                initialZoom: 14.0,
              ),
              children: [
                TileLayer(
                  urlTemplate: 'https://tile.openstreetmap.org/{z}/{x}/{y}.png',
                  userAgentPackageName: 'com.example.nexira_spatial',
                ),
                PolygonLayer(
                  polygons: _fields.map((field) {
                    final isMyField = currentUser != null &&
                        currentUser.role == 'farmer' &&
                        field.farmerId == currentUser.id;

                    return Polygon(
                      points: field.polygonCoordinates,
                      color: isMyField
                          ? Colors.amber.withOpacity(0.5)
                          : Colors.green.withOpacity(0.4),
                      borderColor: isMyField ? Colors.deepOrange : Colors.green[800]!,
                      borderStrokeWidth: isMyField ? 3.0 : 2.0,
                      isFilled: true,
                    );
                  }).toList(),
                ),
                // Interactive markers or tap detectors can be added or handled via onTap on polygons if supported,
                // or markers placed at polygon centroids for tapping.
                MarkerLayer(
                  markers: _fields.where((f) => f.polygonCoordinates.isNotEmpty).map((field) {
                    // compute centroid or use first coordinate
                    final pt = field.polygonCoordinates[0];
                    final isMyField = currentUser != null &&
                        currentUser.role == 'farmer' &&
                        field.farmerId == currentUser.id;

                    return Marker(
                      point: pt,
                      width: 40,
                      height: 40,
                      child: GestureDetector(
                        onTap: () => _showFieldDetails(field),
                        child: Tooltip(
                          message: 'Survey: ${field.surveyNumber}',
                          child: Container(
                            decoration: BoxDecoration(
                              color: isMyField ? Colors.orange : Colors.green[700],
                              shape: BoxShape.circle,
                              border: Border.all(color: Colors.white, width: 2),
                            ),
                            child: const Icon(Icons.grass, color: Colors.white, size: 20),
                          ),
                        ),
                      ),
                    );
                  }).toList(),
                ),
              ],
            ),
      floatingActionButton: FloatingActionButton(
        backgroundColor: const Color(0xFF2E7D32),
        foregroundColor: Colors.white,
        child: const Icon(Icons.center_focus_strong),
        onPressed: () {
          _mapController.move(const LatLng(10.547, 76.290), 14.0);
        },
      ),
    );
  }
}
