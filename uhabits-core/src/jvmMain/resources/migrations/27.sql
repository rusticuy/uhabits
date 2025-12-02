create table Achievements(
    id integer not null primary key autoincrement,
    key string not null unique,
    name string not null,
    description text not null,
    type integer not null default 0,
    streak_target integer,
    habit_uuid string,
    icon string
);

create table AchievementUnlocks(
    id integer not null primary key autoincrement,
    achievement_id integer not null,
    unlocked_at integer not null,
    habit_uuid string,
    foreign key(achievement_id) references Achievements(id) on delete cascade
);

create index idx_AchievementUnlocks_achievement_id on AchievementUnlocks(achievement_id);
create index idx_Achievements_key on Achievements(key);

insert into Achievements(key, name, description, type, streak_target, icon) 
values ('streak_7', '7-Day Streak', 'Complete any habit for 7 days in a row', 0, 7, 'streak_7');

insert into Achievements(key, name, description, type, streak_target, icon) 
values ('streak_30', '30-Day Streak', 'Complete any habit for 30 days in a row', 0, 30, 'streak_30');

insert into Achievements(key, name, description, type, streak_target, icon) 
values ('streak_100', '100-Day Streak', 'Complete any habit for 100 days in a row', 0, 100, 'streak_100');

insert into Achievements(key, name, description, type, streak_target, icon) 
values ('streak_365', '365-Day Streak', 'Complete any habit for 365 days in a row', 0, 365, 'streak_365');
