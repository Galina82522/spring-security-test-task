INSERT INTO public.users (first_name, login, password, access_state)
	VALUES ('Петр', 'admin', '$2a$10$DEzAl9u8/CZc4N7TJn.JQeomV2yvWUdrf9yFKdRfrEC/7h0j2NDse', 'ACTIVE');

INSERT INTO public.users (first_name, last_name, login, password, access_state)
	VALUES ('Иван', 'Алексеев', 'user', '$2a$10$DEzAl9u8/CZc4N7TJn.JQeomV2yvWUdrf9yFKdRfrEC/7h0j2NDse', 'ACTIVE');

INSERT INTO public.users_roles_rel (user_id, role_id) VALUES (1, 1);
INSERT INTO public.users_roles_rel (user_id, role_id) VALUES (2, 2);
