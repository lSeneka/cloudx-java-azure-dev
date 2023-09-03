ALTER SYSTEM SET max_connections = 500;
create user docker with superuser password 'SQL';

create database petstorepetservice_db with owner docker;
create database petstoreproductservice_db with owner docker;