/* Populate USER_PROFILE Table */
INSERT INTO USER_PROFILE(type)
VALUES ('USER');
 
INSERT INTO USER_PROFILE(type)
VALUES ('ADMIN');
 
INSERT INTO USER_PROFILE(type)
VALUES ('DBA');

INSERT INTO USER_PROFILE(type)
VALUES ('GUEST');

/* Populate one Admin User which will further create other users for the application using GUI */
/*password = 123456 */
INSERT INTO APP_USER(login, password, first_name, last_name, email, institution, exclusion_flag)
VALUES ('test','$2a$10$fm0SwxuDCAznM66jOtz4AulxqeMvpBxHnqX0hbYbwu4EXAfqrbl3i', 'Test','User','test@cel.com', 'UNSA', 0);

/* Populate JOIN Table */
INSERT INTO APP_USER_USER_PROFILE (user_id, user_profile_id)
  SELECT user.id, profile.id FROM app_user user, user_profile profile
  where user.login='test' and profile.type='ADMIN';