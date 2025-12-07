-- База данных для цветочного магазина (SQLite)

-- Таблица цветов
CREATE TABLE IF NOT EXISTS flowers (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(100) NOT NULL,
    price INTEGER NOT NULL,
    image_path VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Таблица упаковки
CREATE TABLE IF NOT EXISTS packaging (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(100) NOT NULL,
    price INTEGER NOT NULL,
    image_path VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Таблица декораций
CREATE TABLE IF NOT EXISTS decorations (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(100) NOT NULL,
    price INTEGER NOT NULL,
    image_path VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Таблица букетов
CREATE TABLE IF NOT EXISTS bouquets (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(200),
    total_price INTEGER NOT NULL,
    packaging_id INTEGER REFERENCES packaging(id) ON DELETE SET NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Таблица состава букета (цветы в букете)
CREATE TABLE IF NOT EXISTS bouquet_flowers (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    bouquet_id INTEGER NOT NULL REFERENCES bouquets(id) ON DELETE CASCADE,
    flower_id INTEGER NOT NULL REFERENCES flowers(id) ON DELETE CASCADE,
    quantity INTEGER NOT NULL,
    price_at_moment INTEGER NOT NULL
);

-- Таблица декораций в букете
CREATE TABLE IF NOT EXISTS bouquet_decorations (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    bouquet_id INTEGER NOT NULL REFERENCES bouquets(id) ON DELETE CASCADE,
    decoration_id INTEGER NOT NULL REFERENCES decorations(id) ON DELETE CASCADE,
    price_at_moment INTEGER NOT NULL
);

-- Вставка начальных данных
INSERT OR IGNORE INTO flowers (name, price, image_path) VALUES
    ('Роза', 500, 'images/rose.png'),
    ('Тюльпан', 300, 'images/tulip.png'),
    ('Пион', 700, 'images/peony.png'),
    ('Хризантема', 400, 'images/chrys.png');

INSERT OR IGNORE INTO packaging (name, price, image_path) VALUES
    ('Крафт бумага', 300, 'images/pack_craft.png'),
    ('Белая матовая', 500, 'images/pack_white.png'),
    ('Коробка', 1500, 'images/pack_box.png'),
    ('Шелковая лента', 200, 'images/pack_ribbon.png');

INSERT OR IGNORE INTO decorations (name, price, image_path) VALUES
    ('Блёстки', 200, 'images/deco_glitter.png'),
    ('Сердечки', 150, 'images/deco_heart.png'),
    ('Мини-открытка', 300, 'images/deco_card.png');
