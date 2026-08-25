-- フィーチャーフラグの初期データを登録
INSERT INTO public.feature_flags (key, enabled)
VALUES
  ('WELCOME_EMAIL', true);
