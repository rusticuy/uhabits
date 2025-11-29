create table Goals(
    id integer not null primary key autoincrement,
    name string not null,
    description text,
    target_value real,
    deadline_date integer,
    goal_type integer default 0,
    archived_neq integer default 0,
    created_at integer not null
);

create table GoalHabitLinks(
    id integer not null primary key autoincrement,
    goal_id integer not null,
    habit_id integer not null,
    weight real default 1.0,
    foreign key(goal_id) references Goals(id),
    foreign key(habit_id) references Habits(id)
);

create table GoalMilestones(
    id integer not null primary key autoincrement,
    goal_id integer not null,
    title string not null,
    target_value real not null,
    deadline_date integer,
    completed integer default 0,
    foreign key(goal_id) references Goals(id)
);

create index idx_GoalHabitLinks_goal_id on GoalHabitLinks(goal_id);
create index idx_GoalHabitLinks_habit_id on GoalHabitLinks(habit_id);
create index idx_GoalMilestones_goal_id on GoalMilestones(goal_id);
