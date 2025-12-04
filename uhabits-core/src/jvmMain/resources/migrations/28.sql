create table habit_templates(
    id integer not null primary key autoincrement,
    name string not null,
    description text not null,
    category integer not null default 4,
    freq_num integer not null default 1,
    freq_den integer not null default 1,
    target_value real not null default 0,
    target_type integer not null default 0,
    unit string not null default '',
    icon_key string not null default '',
    color integer not null default 8,
    reminder_hour integer,
    reminder_min integer,
    reminder_days integer,
    position integer not null default 0
);

create index idx_habit_templates_category on habit_templates(category);
create index idx_habit_templates_position on habit_templates(position);

-- Health templates
insert into habit_templates(name, description, category, freq_num, freq_den, target_value, target_type, unit, icon_key, color, reminder_hour, reminder_min, reminder_days, position) 
values ('Drink Water', 'Stay hydrated by drinking water throughout the day', 0, 8, 1, 8.0, 1, 'glasses', 'water', 2, 9, 0, 127, 1);

insert into habit_templates(name, description, category, freq_num, freq_den, target_value, target_type, unit, icon_key, color, reminder_hour, reminder_min, reminder_days, position) 
values ('Exercise', 'Get your body moving with physical activity', 0, 1, 1, 30.0, 1, 'minutes', 'exercise', 1, 18, 0, 127, 2);

insert into habit_templates(name, description, category, freq_num, freq_den, target_value, target_type, unit, icon_key, color, reminder_hour, reminder_min, reminder_days, position) 
values ('Take Vitamins', 'Remember to take your daily vitamins', 0, 1, 1, 0.0, 0, '', 'pills', 3, 8, 0, 127, 3);

-- Learning templates
insert into habit_templates(name, description, category, freq_num, freq_den, target_value, target_type, unit, icon_key, color, reminder_hour, reminder_min, reminder_days, position) 
values ('Read', 'Expand your knowledge by reading books', 1, 1, 1, 30.0, 1, 'minutes', 'book', 4, 20, 0, 127, 4);

insert into habit_templates(name, description, category, freq_num, freq_den, target_value, target_type, unit, icon_key, color, reminder_hour, reminder_min, reminder_days, position) 
values ('Practice Language', 'Improve your language skills with daily practice', 1, 1, 1, 15.0, 1, 'minutes', 'language', 5, 19, 0, 127, 5);

insert into habit_templates(name, description, category, freq_num, freq_den, target_value, target_type, unit, icon_key, color, reminder_hour, reminder_min, reminder_days, position) 
values ('Learn to Code', 'Develop your programming skills', 1, 1, 1, 60.0, 1, 'minutes', 'code', 6, 19, 0, 127, 6);

-- Productivity templates
insert into habit_templates(name, description, category, freq_num, freq_den, target_value, target_type, unit, icon_key, color, reminder_hour, reminder_min, reminder_days, position) 
values ('Meditate', 'Clear your mind with daily meditation', 2, 1, 1, 10.0, 1, 'minutes', 'meditation', 7, 7, 0, 127, 7);

insert into habit_templates(name, description, category, freq_num, freq_den, target_value, target_type, unit, icon_key, color, reminder_hour, reminder_min, reminder_days, position) 
values ('Plan Your Day', 'Set goals and priorities for the day', 2, 1, 1, 0.0, 0, '', 'planning', 8, 7, 0, 127, 8);

insert into habit_templates(name, description, category, freq_num, freq_den, target_value, target_type, unit, icon_key, color, reminder_hour, reminder_min, reminder_days, position) 
values ('No Social Media', 'Reduce distractions by avoiding social media', 2, 1, 1, 0.0, 0, '', 'no_social', 9, 0, 0, 127, 9);

-- Wellness templates
insert into habit_templates(name, description, category, freq_num, freq_den, target_value, target_type, unit, icon_key, color, reminder_hour, reminder_min, reminder_days, position) 
values ('Sleep 8 Hours', 'Ensure you get adequate rest', 3, 1, 1, 8.0, 1, 'hours', 'sleep', 10, 22, 0, 127, 10);

insert into habit_templates(name, description, category, freq_num, freq_den, target_value, target_type, unit, icon_key, color, reminder_hour, reminder_min, reminder_days, position) 
values ('Journal', 'Reflect on your thoughts and experiences', 3, 1, 1, 0.0, 0, '', 'journal', 11, 21, 0, 127, 11);

insert into habit_templates(name, description, category, freq_num, freq_den, target_value, target_type, unit, icon_key, color, reminder_hour, reminder_min, reminder_days, position) 
values ('Gratitude', 'Practice gratitude by writing down what you''re thankful for', 3, 1, 1, 3.0, 1, 'items', 'gratitude', 12, 20, 0, 127, 12);