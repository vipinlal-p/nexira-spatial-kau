import 'package:latlong2/latlong.dart';

class User {
  final int id;
  final String name;
  final String phone;
  final String email;
  final String role;
  final int? krishiBhavanId;

  User({
    required this.id,
    required this.name,
    required this.phone,
    required this.email,
    required this.role,
    this.krishiBhavanId,
  });

  factory User.fromJson(Map<String, dynamic> json) {
    return User(
      id: json['id'],
      name: json['name'],
      phone: json['phone'],
      email: json['email'],
      role: json['role'],
      krishiBhavanId: json['krishi_bhavan_id'],
    );
  }
}

class PaddyFieldFeature {
  final int id;
  final String surveyNumber;
  final int farmerId;
  final String farmerName;
  final String farmerPhone;
  final int krishiBhavanId;
  final double areaHa;
  final String cropSeason;
  final String soilType;
  final Map<String, dynamic> attributes;
  final List<LatLng> polygonCoordinates;

  PaddyFieldFeature({
    required this.id,
    required this.surveyNumber,
    required this.farmerId,
    required this.farmerName,
    required this.farmerPhone,
    required this.krishiBhavanId,
    required this.areaHa,
    required this.cropSeason,
    required this.soilType,
    required this.attributes,
    required this.polygonCoordinates,
  });

  factory PaddyFieldFeature.fromJson(Map<String, dynamic> json) {
    final props = json['properties'] ?? {};
    final geom = json['geometry'] ?? {};
    final coords = geom['coordinates'] as List? ?? [];
    
    List<LatLng> latLngs = [];
    if (coords.isNotEmpty && geom['type'] == 'MultiPolygon') {
      // Extract first ring of the first polygon in MultiPolygon
      try {
        final polygonRing = coords[0][0] as List;
        for (var pt in polygonRing) {
          // GeoJSON coordinates are [longitude, latitude]
          latLngs.add(LatLng(pt[1] as double, pt[0] as double));
        }
      } catch (_) {}
    } else if (coords.isNotEmpty && geom['type'] == 'Polygon') {
      try {
        final polygonRing = coords[0] as List;
        for (var pt in polygonRing) {
          latLngs.add(LatLng(pt[1] as double, pt[0] as double));
        }
      } catch (_) {}
    }

    return PaddyFieldFeature(
      id: props['id'] ?? 0,
      surveyNumber: props['survey_number'] ?? '',
      farmerId: props['farmer_id'] ?? 0,
      farmerName: props['farmer_name'] ?? 'Unknown',
      farmerPhone: props['farmer_phone'] ?? '',
      krishiBhavanId: props['krishi_bhavan_id'] ?? 0,
      areaHa: (props['area_ha'] ?? 0.0).toDouble(),
      cropSeason: props['crop_season'] ?? '',
      soilType: props['soil_type'] ?? '',
      attributes: props['attributes'] ?? {},
      polygonCoordinates: latLngs,
    );
  }
}

class Announcement {
  final int id;
  final int officerId;
  final int krishiBhavanId;
  final String title;
  final String body;
  final String createdAt;

  Announcement({
    required this.id,
    required this.officerId,
    required this.krishiBhavanId,
    required this.title,
    required this.body,
    required this.createdAt,
  });

  factory Announcement.fromJson(Map<String, dynamic> json) {
    return Announcement(
      id: json['id'],
      officerId: json['officer_id'],
      krishiBhavanId: json['krishi_bhavan_id'],
      title: json['title'],
      body: json['body'],
      createdAt: json['created_at'] ?? '',
    );
  }
}

class MessageModel {
  final int id;
  final int senderId;
  final int receiverId;
  final String body;
  final String createdAt;
  final String? readAt;

  MessageModel({
    required this.id,
    required this.senderId,
    required this.receiverId,
    required this.body,
    required this.createdAt,
    this.readAt,
  });

  factory MessageModel.fromJson(Map<String, dynamic> json) {
    return MessageModel(
      id: json['id'],
      senderId: json['sender_id'],
      receiverId: json['receiver_id'],
      body: json['body'],
      createdAt: json['created_at'] ?? '',
      readAt: json['read_at'],
    );
  }
}
