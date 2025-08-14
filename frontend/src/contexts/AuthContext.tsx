import React, { createContext, useContext, useState, useEffect, type ReactNode } from 'react';

interface User {
  id: number;
  username: string;
  email?: string;
  is_active: boolean;
  is_admin: boolean;
}

interface AuthContextType {
  user: User | null;
  token: string | null;
  login: (username: string, password: string) => Promise<boolean>;
  logout: () => void;
  isAuthenticated: boolean;
  loading: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  // 从localStorage恢复认证状态
  useEffect(() => {
    const savedToken = localStorage.getItem('auth_token');
    const savedUser = localStorage.getItem('auth_user');
    
    if (savedToken && savedUser) {
      try {
        setToken(savedToken);
        setUser(JSON.parse(savedUser));
      } catch (error) {
        console.error('Failed to parse saved user data:', error);
        localStorage.removeItem('auth_token');
        localStorage.removeItem('auth_user');
      }
    }
    setLoading(false);
  }, []);

  // 验证token有效性 - 只在token存在时验证
  useEffect(() => {
    if (token && !user) {
      validateToken();
    }
  }, [token, user]);

  const validateToken = async () => {
    if (!token) return;
    
    try {
      const response = await fetch('/api/auth/me', {
        headers: {
          'token': token,
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        throw new Error('Token validation failed');
      }

      const responseData = await response.json();
      if (responseData.code === 1 && responseData.data) {
        setUser(responseData.data);
        localStorage.setItem('auth_user', JSON.stringify(responseData.data));
      } else {
        throw new Error(responseData.msg || 'Token validation failed');
      }
    } catch (error) {
      console.error('Token validation failed:', error);
      logout();
    }
  };

  const login = async (username: string, password: string): Promise<boolean> => {
    try {
      const response = await fetch('/api/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username, password }),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.msg || '登录失败');
      }

      const data = await response.json();
      
      // 新的后端返回格式：{"code":1,"msg":"success","data":"token字符串"}
      if (data.code === 1 && data.data) {
        // 设置token
        setToken(data.data);
        localStorage.setItem('auth_token', data.data);
        
        // 设置临时用户信息
        const tempUser = { 
          id: 0,
          username: username,
          is_active: true,
          is_admin: false
        };
        setUser(tempUser);
        
        // 获取完整的用户信息
        try {
          const userResponse = await fetch('/api/auth/me', {
            headers: {
              'token': data.data,
              'Content-Type': 'application/json',
            },
          });

          if (userResponse.ok) {
            const userResponseData = await userResponse.json();
            if (userResponseData.code === 1 && userResponseData.data) {
              setUser(userResponseData.data);
              localStorage.setItem('auth_user', JSON.stringify(userResponseData.data));
            }
          }
        } catch (error) {
          console.error('Failed to get user info:', error);
          // 即使获取用户信息失败，也保持登录状态
        }
        
        return true;
      } else {
        throw new Error(data.msg || '登录失败');
      }
    } catch (error) {
      console.error('Login failed:', error);
      return false;
    }
  };

  const logout = () => {
    setUser(null);
    setToken(null);
    localStorage.removeItem('auth_token');
    localStorage.removeItem('auth_user');
  };

  const value: AuthContextType = {
    user,
    token,
    login,
    logout,
    isAuthenticated: !!user && !!token,
    loading,
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
}; 