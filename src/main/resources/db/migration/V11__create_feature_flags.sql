-- フィーチャーフラグ
CREATE TABLE feature_flags (
    key VARCHAR(255) PRIMARY KEY,   -- フィーチャーキー
    enabled BOOLEAN NOT NULL,      -- 有効状態
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 登録日時
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP  -- 更新日時
);
