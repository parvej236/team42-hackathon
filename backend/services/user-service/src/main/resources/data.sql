-- Initial Data Seed for user_schema.users
INSERT INTO users (id, name, email, avatar, role, provider)
VALUES 
  (1, 'System Administrator', 'admin@nexusmart.com', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop&q=80', 'ROLE_ADMIN', 'LOCAL'),
  (2, 'Store Operations Manager', 'manager@nexusmart.com', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&auto=format&fit=crop&q=80', 'ROLE_MANAGER', 'LOCAL'),
  (3, 'Alex Johnson', 'alex.johnson@gmail.com', 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150&auto=format&fit=crop&q=80', 'ROLE_USER', 'GOOGLE')
ON CONFLICT (id) DO NOTHING;
