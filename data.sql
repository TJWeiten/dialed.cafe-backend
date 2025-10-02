-------------------------

-- Seed the users table
-- Note: UUIDs are provided explicitly to allow for foreign key relationships in other tables.

INSERT INTO users (id, oauth_provider, oauth_id, email, first_name, last_name, created_at) VALUES
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'apple', '101234567890123456789', 'tsubaki@kouryuu.me', 'Tsubaki', 'Kouryuu', CURRENT_TIMESTAMP);
INSERT INTO users (id, oauth_provider, oauth_id, email, first_name, last_name, created_at) VALUES
('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'apple', '987654321', 'hello@tjweiten.com', 'T.J.', 'Weiten', CURRENT_TIMESTAMP);
INSERT INTO users (id, oauth_provider, oauth_id, email, first_name, last_name, created_at) VALUES
('c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'google', '109876543210987654321', 'alex.ray@example.com', 'Alex', NULL, CURRENT_TIMESTAMP);

-------------------------

--- Seed the beans table, linking to the users created previously

INSERT INTO beans (id, user_id, name, roaster, roast_level, package_weight, current_weight, is_decaf, process, roast_date, descriptors, notes, is_archived) VALUES
('d1eebc99-9c0b-4ef8-bb6d-6bb9bd380b11', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Ethiopia Guji Natural', 'Stumptown', 'LIGHT', 340, 150.5, false, 'NATURAL', '2025-09-18', 'Blueberry, Floral, Jasmine', 'Really vibrant and fruity. Great for pour-over.', false),
('d2eebc99-9c0b-4ef8-bb6d-6bb9bd380b12', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Colombia La Palma', 'Heart Roasters', 'MEDIUM', 250, 200.0, false, 'WASHED', '2025-09-22', 'Caramel, Green Apple, Balanced', 'A solid daily driver coffee. Smooth and sweet.', false);
INSERT INTO beans (id, user_id, name, roaster, roast_level, package_weight, current_weight, is_decaf, process, roast_date, descriptors, notes, is_archived) VALUES
('d3eebc99-9c0b-4ef8-bb6d-6bb9bd380b13', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Brazil Santos', 'Peet''s Coffee', 'MEDIUM_DARK', 500, 0.0, false, 'NATURAL', '2025-07-01', 'Nutty, Chocolate, Low Acidity', 'Finished this bag a while ago. A classic dark roast.', true),
('d4eebc99-9c0b-4ef8-bb6d-6bb9bd380b14', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Decaf Mexico Chiapas', 'Sightglass', 'MEDIUM', 340, 0.0, true, 'WASHED', '2025-08-15', 'Brown Sugar, Mild Citrus', 'Good for an evening cup.', true);

INSERT INTO beans (id, user_id, name, roaster, roast_level, package_weight, current_weight, is_decaf, process, roast_date, descriptors, notes, is_archived) VALUES
('e1eebc99-9c0b-4ef8-bb6d-6bb9bd380c11', 'b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'Kenya AA Nyeri', 'Blue Bottle', 'LIGHT', 300, 250.0, false, 'WASHED', '2025-09-25', 'Blackcurrant, Grapefruit, Bright', 'Very bright and acidic, almost juicy.', false),
('e2eebc99-9c0b-4ef8-bb6d-6bb9bd380c12', 'b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'Sumatra Mandheling', 'Local Roaster', 'DARK', 454, 300.2, false, 'WET_HULLED', '2025-09-10', 'Earthy, Tobacco, Cedar', 'Classic Sumatran profile. Makes a great French press.', false),
('e3eebc99-9c0b-4ef8-bb6d-6bb9bd380c13', 'b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'Guatemala Huehuetenango', 'Intelligentsia', 'MEDIUM', 340, 100.0, false, 'WASHED', '2025-09-05', 'Chocolate, Toffee, Orange', 'Very balanced and drinkable.', false);
INSERT INTO beans (id, user_id, name, roaster, roast_level, package_weight, current_weight, is_decaf, process, roast_date, descriptors, notes, is_archived) VALUES
('e4eebc99-9c0b-4ef8-bb6d-6bb9bd380c14', 'b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'Costa Rica Honey Process', 'Verve Coffee', 'MEDIUM', 340, 0.0, false, 'HONEY', '2025-06-20', 'Honey, Raisin, Silky', 'Really enjoyed the honey process on this one.', true),
('e5eebc99-9c0b-4ef8-bb6d-6bb9bd380c15', 'b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'Espresso Blend', 'Illy', 'MEDIUM_DARK', 250, 0.0, false, 'WASHED', '2025-05-10', 'Caramel, Nutty, Robust', 'Used for espresso shots.', true);

INSERT INTO beans (id, user_id, name, roaster, roast_level, package_weight, current_weight, is_decaf, process, roast_date, descriptors, notes, is_archived) VALUES
('f1eebc99-9c0b-4ef8-bb6d-6bb9bd380d11', 'c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'Panama Geisha', 'Proud Mary', 'LIGHT', 150, 50.0, false, 'WASHED', '2025-09-28', 'Jasmine, Bergamot, Tropical Fruit', 'Very expensive, but worth it. The aroma is incredible.', false);
INSERT INTO beans (id, user_id, name, roaster, roast_level, package_weight, current_weight, is_decaf, process, roast_date, descriptors, notes, is_archived) VALUES
('f2eebc99-9c0b-4ef8-bb6d-6bb9bd380d12', 'c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'Holiday Blend 2024', 'Starbucks', 'DARK', 454, 0.0, false, 'WASHED', '2025-01-05', 'Spicy, Sweet, Full-bodied', 'A gift from last Christmas.', true),
('f3eebc99-9c0b-4ef8-bb6d-6bb9bd380d13', 'c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'Nicaragua Jinotega', 'Trader Joe''s', 'MEDIUM', 368, 0.0, false, 'WASHED', '2025-07-20', 'Mild, Clean, Nutty', 'A decent budget coffee.', true);

-------------------------

-- Seed the grinders table, linking to the users created previously

INSERT INTO grinders (id, user_id, name, burr_type, is_stepless, grind_range, notes) VALUES
(RANDOM_UUID(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Mazzer Philos', 'FLAT', false, '1-50', 'My primary grinder for daily espresso shots. Very consistent.'),
(RANDOM_UUID(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Comandante C40', 'CONICAL', true, '1-12', 'Excellent hand grinder that I use for pour-overs and when traveling.');

INSERT INTO grinders (id, user_id, name, burr_type, is_stepless, grind_range, notes) VALUES
(RANDOM_UUID(), 'b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'Mazzer Philos', 'FLAT', true, '1 to 40', 'Just got this grinder. Still working on dialing in the perfect espresso grind setting.');

INSERT INTO grinders (id, user_id, name, burr_type, is_stepless, grind_range, notes) VALUES
(RANDOM_UUID(), 'c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 'Baratza Encore', 'CONICAL', false, '9 to 18', 'A reliable workhorse grinder for my daily drip coffee.');

-------------------------
