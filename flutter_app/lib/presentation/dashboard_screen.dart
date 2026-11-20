import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:nexira_spatial/core/auth_provider.dart';
import 'package:nexira_spatial/presentation/map_screen.dart';
import 'package:nexira_spatial/presentation/announcements_screen.dart';
import 'package:nexira_spatial/presentation/messages_screen.dart';

class DashboardScreen extends ConsumerStatefulWidget {
  const DashboardScreen({super.key});

  @override
  ConsumerState<DashboardScreen> createState() => _DashboardScreenState();
}

class _DashboardScreenState extends ConsumerState<DashboardScreen> {
  int _currentIndex = 0;

  final List<Widget> _screens = [
    const MapScreen(),
    const AnnouncementsScreen(),
    const MessagesScreen(),
  ];

  @override
  Widget build(BuildContext context) {
    final user = ref.watch(authProvider).user;

    return LayoutBuilder(
      builder: (context, constraints) {
        final isWideScreen = constraints.maxWidth >= 800;

        if (isWideScreen) {
          // Web / Desktop responsive layout with NavigationRail
          return Scaffold(
            body: Row(
              children: [
                NavigationRail(
                  selectedIndex: _currentIndex,
                  onDestinationSelected: (index) => setState(() => _currentIndex = index),
                  backgroundColor: const Color(0xFF1B5E20),
                  selectedIconTheme: const IconThemeData(color: Colors.amber, size: 28),
                  unselectedIconTheme: const IconThemeData(color: Colors.white75, size: 24),
                  selectedLabelTextStyle: const TextStyle(color: Colors.amber, fontWeight: FontWeight.bold),
                  unselectedLabelTextStyle: const TextStyle(color: Colors.white75),
                  labelType: NavigationRailLabelType.all,
                  leading: Padding(
                    padding: const EdgeInsets.symmetric(vertical: 16.0),
                    child: Column(
                      children: [
                        const Icon(Icons.grass, color: Colors.white, size: 36),
                        const SizedBox(height: 8),
                        Text(
                          user?.name ?? 'KAU User',
                          style: const TextStyle(color: Colors.white, fontSize: 12, fontWeight: FontWeight.bold),
                          textAlign: TextAlign.center,
                        ),
                      ],
                    ),
                  ),
                  trailing: Expanded(
                    child: Align(
                      alignment: Alignment.bottomCenter,
                      child: Padding(
                        padding: const EdgeInsets.only(bottom: 24.0),
                        child: IconButton(
                          icon: const Icon(Icons.logout, color: Colors.white),
                          tooltip: 'Logout',
                          onPressed: () => ref.read(authProvider.notifier).logout(),
                        ),
                      ),
                    ),
                  ),
                  destinations: const [
                    NavigationRailDestination(
                      icon: Icon(Icons.map),
                      label: Text('GIS Map'),
                    ),
                    NavigationRailDestination(
                      icon: Icon(Icons.announcement),
                      label: Text('Announcements'),
                    ),
                    NavigationRailDestination(
                      icon: Icon(Icons.chat),
                      label: Text('Messages'),
                    ),
                  ],
                ),
                Expanded(
                  child: _screens[_currentIndex],
                ),
              ],
            ),
          );
        }

        // Mobile layout with BottomNavigationBar
        return Scaffold(
          body: _screens[_currentIndex],
          bottomNavigationBar: BottomNavigationBar(
            currentIndex: _currentIndex,
            onTap: (index) => setState(() => _currentIndex = index),
            selectedItemColor: const Color(0xFF1B5E20),
            unselectedItemColor: Colors.grey,
            items: const [
              BottomNavigationBarItem(
                icon: Icon(Icons.map),
                label: 'GIS Map',
              ),
              BottomNavigationBarItem(
                icon: Icon(Icons.announcement),
                label: 'Announcements',
              ),
              BottomNavigationBarItem(
                icon: Icon(Icons.chat),
                label: 'Messages',
              ),
            ],
          ),
          floatingActionButton: FloatingActionButton(
            mini: true,
            backgroundColor: Colors.red[700],
            foregroundColor: Colors.white,
            tooltip: 'Logout',
            child: const Icon(Icons.logout),
            onPressed: () => ref.read(authProvider.notifier).logout(),
          ),
        );
      },
    );
  }
}
