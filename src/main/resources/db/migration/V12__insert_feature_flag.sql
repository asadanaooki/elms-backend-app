-- フィーチャーフラグ
INSERT INTO public.feature_flags
(key, enabled, created_at, updated_at) 
VALUES
('WELCOME_EMAIL', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
