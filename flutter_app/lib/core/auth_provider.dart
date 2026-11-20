import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:nexira_spatial/core/api_client.dart';
import 'package:nexira_spatial/data/models.dart';

class AuthState {
  final bool isAuthenticated;
  final User? user;
  final String? token;
  final String? errorMessage;

  AuthState({
    required this.isAuthenticated,
    this.user,
    this.token,
    this.errorMessage,
  });

  AuthState copyWith({
    bool? isAuthenticated,
    User? user,
    String? token,
    String? errorMessage,
  }) {
    return AuthState(
      isAuthenticated: isAuthenticated ?? this.isAuthenticated,
      user: user ?? this.user,
      token: token ?? this.token,
      errorMessage: errorMessage,
    );
  }
}

class AuthNotifier extends StateNotifier<AuthState> {
  AuthNotifier() : super(AuthState(isAuthenticated: false)) {
    checkSavedSession();
  }

  final _dio = ApiClient().dio;

  Future<void> checkSavedSession() async {
    final prefs = await SharedPreferences.getInstance();
    final token = prefs.getString('jwt_token');
    final userId = prefs.getInt('user_id');
    final name = prefs.getString('user_name');
    final role = prefs.getString('user_role');

    if (token != null && userId != null) {
      try {
        final response = await _dio.get('/auth/me');
        final user = User.fromJson(response.data);
        state = AuthState(
          isAuthenticated: true,
          user: user,
          token: token,
        );
      } catch (_) {
        // Fallback to local stored info if offline
        state = AuthState(
          isAuthenticated: true,
          user: User(
            id: userId,
            name: name ?? 'User',
            phone: '',
            email: '',
            role: role ?? 'farmer',
          ),
          token: token,
        );
      }
    }
  }

  Future<bool> login(String email, String password) async {
    try {
      final response = await _dio.post('/auth/login', data: {
        'email': email,
        'password': password,
      });

      final data = response.data;
      final token = data['access_token'];
      final role = data['role'];
      final userId = data['user_id'];
      final name = data['name'];

      final prefs = await SharedPreferences.getInstance();
      await prefs.setString('jwt_token', token);
      await prefs.setInt('user_id', userId);
      await prefs.setString('user_name', name);
      await prefs.setString('user_role', role);

      // Fetch full user profile
      final meResp = await _dio.get('/auth/me', options: Options(headers: {'Authorization': 'Bearer $token'}));
      final user = User.fromJson(meResp.data);

      state = AuthState(
        isAuthenticated: true,
        user: user,
        token: token,
      );
      return true;
    } catch (e) {
      state = state.copyWith(errorMessage: 'Login failed: ${e.toString()}');
      return false;
    }
  }

  Future<void> logout() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.clear();
    state = AuthState(isAuthenticated: false);
  }
}

final authProvider = StateNotifierProvider<AuthNotifier, AuthState>((ref) {
  return AuthNotifier();
});
