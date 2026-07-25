-- 教材で利用する既存タグ
INSERT INTO public.tags (name)
VALUES
    -- 技術・ツール
    ('Java'),
    ('Git'),
    ('GitHub'),
    ('SQL'),
    ('Spring'),
    ('Spring Data JDBC'),
    ('Docker'),
    ('Docker Compose'),
    ('PostgreSQL'),
    ('REST API'),

    -- 学習トピック
    ('オブジェクト指向'),
    ('コレクション'),
    ('Stream API'),
    ('DI'),
    ('バリデーション'),
    ('トランザクション'),
    ('データベース'),
    ('バージョン管理'),
    ('環境構築'),

    -- 難易度・学習段階
    ('入門'),
    ('基礎'),
    ('応用'),
    ('実践')
ON CONFLICT (name) DO NOTHING;

-- 各コースから内容が明確なレッスンを選び、関連するタグを紐づける
-- 一部のレッスンはタグなしとし、必要以上にタグを付与しない
INSERT INTO public.lesson_tags (lesson_id, tag_id)
SELECT l.id, t.id
FROM (
    VALUES
        -- 1章:Java基礎（10 / 18レッスン）
        (10, 14, ARRAY['Java', '環境構築']::VARCHAR[]),
        (10, 13, ARRAY['Java', '環境構築']),
        (10, 12, ARRAY['Java', '入門']),
        (10, 20, ARRAY['Java', '基礎']),
        (10, 18, ARRAY['Java', '基礎']),
        (10, 16, ARRAY['Java', '基礎']),
        (10, 28, ARRAY['Java', 'オブジェクト指向']),
        (10, 27, ARRAY['Java', 'オブジェクト指向', '実践']),
        (10, 26, ARRAY['Java', 'オブジェクト指向']),
        (10, 24, ARRAY['Java', '実践']),

        -- 2章:Git（8 / 12レッスン）
        (11, 30, ARRAY['Git', 'GitHub', '入門', 'バージョン管理']),
        (11, 29, ARRAY['Git', '環境構築']),
        (11, 34, ARRAY['Git', 'バージョン管理']),
        (11, 33, ARRAY['Git', 'バージョン管理']),
        (11, 39, ARRAY['GitHub', '実践']),
        (11, 38, ARRAY['Git', 'GitHub']),
        (11, 37, ARRAY['Git', '実践']),
        (11, 40, ARRAY['Git', '基礎']),

        -- 3章:Java応用（9 / 16レッスン）
        (12, 48, ARRAY['Java', 'オブジェクト指向', '応用']),
        (12, 45, ARRAY['Java', 'オブジェクト指向', '応用']),
        (12, 44, ARRAY['Java', 'オブジェクト指向', '応用']),
        (12, 43, ARRAY['Java', 'コレクション']),
        (12, 42, ARRAY['Java', '応用']),
        (12, 55, ARRAY['Java', '応用']),
        (12, 54, ARRAY['Java', 'Stream API']),
        (12, 53, ARRAY['Java', '応用']),
        (12, 56, ARRAY['Java', '実践']),

        -- 4章:SQL（8 / 11レッスン）
        (13, 57, ARRAY['SQL', 'データベース', '入門']),
        (13, 60, ARRAY['SQL', '基礎']),
        (13, 61, ARRAY['SQL', 'データベース']),
        (13, 62, ARRAY['SQL', '応用']),
        (13, 63, ARRAY['SQL', 'データベース']),
        (13, 65, ARRAY['SQL', '応用']),
        (13, 67, ARRAY['SQL', 'トランザクション']),
        (13, 66, ARRAY['SQL', '実践']),

        -- 5章:Spring（14 / 21レッスン）
        (14, 72, ARRAY['Spring', '入門']),
        (14, 71, ARRAY['Spring', '環境構築']),
        (14, 69, ARRAY['Spring', 'REST API']),
        (14, 68, ARRAY['Spring', 'REST API', '実践']),
        (14, 74, ARRAY['Spring', 'DI']),
        (14, 73, ARRAY['Spring', 'DI']),
        (14, 78, ARRAY['Spring Data JDBC', 'PostgreSQL', 'データベース']),
        (14, 77, ARRAY['Spring', 'REST API']),
        (14, 76, ARRAY['Spring Data JDBC', 'REST API']),
        (14, 75, ARRAY['Spring Data JDBC', 'REST API']),
        (14, 85, ARRAY['Spring', 'バリデーション']),
        (14, 87, ARRAY['Spring', 'トランザクション']),
        (14, 86, ARRAY['Spring', 'トランザクション', '実践']),
        (14, 88, ARRAY['Spring', 'Spring Data JDBC', 'データベース']),

        -- 6章:Docker（10 / 14レッスン）
        (15, 90, ARRAY['Docker', '入門']),
        (15, 89, ARRAY['Docker', '環境構築']),
        (15, 91, ARRAY['Docker', '基礎']),
        (15, 94, ARRAY['Docker', '実践']),
        (15, 96, ARRAY['Docker', '実践']),
        (15, 99, ARRAY['Docker', '基礎']),
        (15, 97, ARRAY['Docker', '実践']),
        (15, 100, ARRAY['Docker', '応用']),
        (15, 101, ARRAY['Docker', 'Docker Compose']),
        (15, 102, ARRAY['Docker', 'Docker Compose', 'REST API', '実践'])
) AS seed(course_id, lesson_id, tag_names)
JOIN public.lessons l
  ON l.id = seed.lesson_id
 AND l.course_id = seed.course_id
CROSS JOIN LATERAL UNNEST(seed.tag_names) AS seeded_tag(name)
JOIN public.tags t ON t.name = seeded_tag.name
ON CONFLICT (lesson_id, tag_id) DO NOTHING;
