-- Core tables
create table interviewer (
    id bigint primary key auto_increment,
    name varchar(200) not null,
    email varchar(320) not null unique,
    created_at timestamp not null default current_timestamp
);

create table candidate (
    id bigint primary key auto_increment,
    name varchar(200) not null,
    email varchar(320) not null unique,
    created_at timestamp not null default current_timestamp
);

create table availability_rule (
    id bigint primary key auto_increment,
    interviewer_id bigint not null,
    day_of_week tinyint not null, -- 1=Monday .. 7=Sunday (ISO)
    start_time time not null,
    end_time time not null,
    slot_minutes smallint not null,
    active boolean not null default true,
    created_at timestamp not null default current_timestamp,
    constraint fk_availability_rule_interviewer foreign key (interviewer_id) references interviewer(id)
);

create table weekly_cap (
    id bigint primary key auto_increment,
    interviewer_id bigint not null,
    week_start_date date not null,
    max_interviews int not null,
    created_at timestamp not null default current_timestamp,
    constraint uq_weekly_cap unique (interviewer_id, week_start_date),
    constraint fk_weekly_cap_interviewer foreign key (interviewer_id) references interviewer(id)
);

create table generated_slot (
    id bigint primary key auto_increment,
    interviewer_id bigint not null,
    start_at timestamp not null,
    end_at timestamp not null,
    status varchar(16) not null default 'OPEN',
    version bigint not null default 0,
    constraint uq_slot_start unique (interviewer_id, start_at),
    constraint fk_slot_interviewer foreign key (interviewer_id) references interviewer(id)
);
create index idx_slot_status_start on generated_slot(status, start_at, interviewer_id);

create table booking (
    id bigint primary key auto_increment,
    candidate_id bigint not null,
    interviewer_id bigint not null,
    slot_id bigint not null,
    status varchar(16) not null,
    created_at timestamp not null default current_timestamp,
    updated_at timestamp null,
    version bigint not null default 0,
    constraint fk_booking_candidate foreign key (candidate_id) references candidate(id),
    constraint fk_booking_interviewer foreign key (interviewer_id) references interviewer(id),
    constraint fk_booking_slot foreign key (slot_id) references generated_slot(id)
);
create unique index uq_booking_slot on booking(slot_id);

create table weekly_counter (
    id bigint primary key auto_increment,
    interviewer_id bigint not null,
    week_start_date date not null,
    confirmed_count int not null default 0,
    version bigint not null default 0,
    constraint uq_weekly_counter unique (interviewer_id, week_start_date),
    constraint fk_weekly_counter_interviewer foreign key (interviewer_id) references interviewer(id)
);

create table idempotency_key (
    id bigint primary key auto_increment,
    scope varchar(64) not null,
    key_hash varchar(128) not null,
    response_hash varchar(256) null,
    created_at timestamp not null default current_timestamp,
    constraint uq_idem unique (scope, key_hash)
);
