import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:nexira_spatial/core/auth_provider.dart';
import 'package:nexira_spatial/presentation/login_screen.dart';
import 'package:nexira_spatial/presentation/dashboard_screen.dart';

void main() {
  runApp(
    const ProviderScope(
      child: NexiraSpatialApp(),
    ),
  );
}

class NexiraSpatialApp extends ConsumerWidget {
  const NexiraSpatialApp({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final authState = ref.watch(authProvider);

    return MaterialApp(
      title: 'Nexira Spatial – KAU Paddy GIS',
      theme: ThemeData(
        primarySwatch: Colors.green,
        scaffoldBackgroundColor: Colors.white,
        useMaterial3: true,
      ),
      home: authState.isAuthenticated ? const DashboardScreen() : const LoginScreen(),
      debugShowCheckedModeBanner: false,
    );
  }
}
