select * from useraccount
where
(username = :username)
and
(password = :password);
