create table Goals (
    id integer primary key autoincrement,
    uuid text,
    name text,
    description text,
    color integer,
    archived integer,
    position integer,
    due_date integer
);

create table GoalHabitLinks (
    id integer primary key autoincrement,
    goal_id integer references Goals(id),
    habit_id integer references habits(id),
    weight real
);

create table GoalMilestones (
    id integer primary key autoincrement,
    uuid text,
    goal_id integer references Goals(id),
    name text,
    description text,
    target_value real,
    due_date integer,
    position integer,
    is_completed integer
);
