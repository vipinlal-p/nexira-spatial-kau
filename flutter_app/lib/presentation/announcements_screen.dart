import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:nexira_spatial/core/api_client.dart';
import 'package:nexira_spatial/core/auth_provider.dart';
import 'package:nexira_spatial/data/models.dart';

class AnnouncementsScreen extends ConsumerStatefulWidget {
  const AnnouncementsScreen({super.key});

  @override
  ConsumerState<AnnouncementsScreen> createState() => _AnnouncementsScreenState();
}

class _AnnouncementsScreenState extends ConsumerState<AnnouncementsScreen> {
  List<Announcement> _announcements = [];
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _fetchAnnouncements();
  }

  Future<void> _fetchAnnouncements() async {
    setState(() => _isLoading = true);
    try {
      final response = await ApiClient().dio.get('/announcements');
      final list = (response.data as List).map((e) => Announcement.fromJson(e)).toList();
      setState(() {
        _announcements = list;
        _isLoading = false;
      });
    } catch (e) {
      setState(() => _isLoading = false);
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Failed to load announcements: $e')),
        );
      }
    }
  }

  void _showCreateDialog() {
    final titleController = TextEditingController();
    final bodyController = TextEditingController();

    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Broadcast Announcement'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            TextField(
              controller: titleController,
              decoration: const InputDecoration(labelText: 'Title'),
            ),
            const SizedBox(height: 12),
            TextField(
              controller: bodyController,
              maxLines: 4,
              decoration: const InputDecoration(labelText: 'Announcement Details'),
            ),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Cancel'),
          ),
          ElevatedButton(
            style: ElevatedButton.styleFrom(backgroundColor: const Color(0xFF2E7D32), foregroundColor: Colors.white),
            onPressed: () async {
              try {
                final user = ref.read(authProvider).user;
                await ApiClient().dio.post('/announcements', data: {
                  'title': titleController.text,
                  'body': bodyController.text,
                  'krishi_bhavan_id': user?.krishiBhavanId ?? 1,
                });
                Navigator.pop(context);
                _fetchAnnouncements();
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text('Announcement broadcasted successfully')),
                );
              } catch (e) {
                ScaffoldMessenger.of(context).showSnackBar(
                  SnackBar(content: Text('Failed to broadcast: $e')),
                );
              }
            },
            child: const Text('Broadcast'),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final user = ref.watch(authProvider).user;
    final isOfficerOrAdmin = user?.role == 'officer' || user?.role == 'admin';

    return Scaffold(
      appBar: AppBar(
        title: const Text('Krishi Bhavan Announcements'),
        backgroundColor: const Color(0xFF1B5E20),
        foregroundColor: Colors.white,
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : _announcements.isEmpty
              ? const Center(child: Text('No announcements available.'))
              : ListView.builder(
                  padding: const EdgeInsets.all(16),
                  itemCount: _announcements.length,
                  itemBuilder: (context, index) {
                    final item = _announcements[index];
                    return Card(
                      elevation: 3,
                      margin: const EdgeInsets.only(bottom: 12),
                      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                      child: Padding(
                        padding: const EdgeInsets.all(16.0),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Row(
                              mainAxisAlignment: MainAxisAlignment.between,
                              children: [
                                Expanded(
                                  child: Text(
                                    item.title,
                                    style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold, color: Color(0xFF1B5E20)),
                                  ),
                                ),
                                Text(
                                  item.createdAt.substring(0, 10),
                                  style: const TextStyle(fontSize: 12, color: Colors.grey),
                                ),
                              ],
                            ),
                            const SizedBox(height: 8),
                            Text(item.body, style: const TextStyle(fontSize: 14)),
                          ],
                        ),
                      ),
                    );
                  },
                ),
      floatingActionButton: isOfficerOrAdmin
          ? FloatingActionButton.extended(
              backgroundColor: const Color(0xFF2E7D32),
              foregroundColor: Colors.white,
              icon: const Icon(Icons.announcement),
              label: const Text('Broadcast'),
              onPressed: _showCreateDialog,
            )
          : null,
    );
  }
}
