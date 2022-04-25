BEGIN;
-- users

INSERT INTO users (usr_name, usr_email, usr_password, usr_type)
	VALUES ('Felipe Silva', 'felipe@email.com', '$2b$10$SM3l2psCKsDHqIVmVkvpiu65KlrVhMD16pnuBMUB5mHyT6rdnBo2C', 'admin');
INSERT INTO users (usr_name, usr_email, usr_password, usr_type)
	VALUES ('Willian Santa Ana', 'willian@email.com', '$2b$10$SM3l2psCKsDHqIVmVkvpiu65KlrVhMD16pnuBMUB5mHyT6rdnBo2C', 'admin');
INSERT INTO users (usr_name, usr_email, usr_password)
	VALUES ('Jacinto Estima', 'jacinto@email.com', '$2b$10$SM3l2psCKsDHqIVmVkvpiu65KlrVhMD16pnuBMUB5mHyT6rdnBo2C');
INSERT INTO users (usr_name, usr_email, usr_password)
	VALUES ('Miguel Boavida', 'miguel@email.com', '$2b$10$SM3l2psCKsDHqIVmVkvpiu65KlrVhMD16pnuBMUB5mHyT6rdnBo2C');

-- street_arts

INSERT INTO street_arts (sta_usr_id, sta_artist, sta_project, sta_year, sta_photo_credits, sta_address, sta_coords, sta_status)
  VALUES (
    1,
	'MAR',
	'Rostos do Muro Azul',
	2012,
	'© CML | DMC | DPC | José Vicente 2013',
	'Rua das Murtas',
	'38.760555, -9.148055',
	'existente'
);
INSERT INTO street_arts (sta_usr_id, sta_artist, sta_project, sta_year, sta_photo_credits, sta_address, sta_coords, sta_status)
  VALUES (
    1,
	'Tamara Alves',
	'Festival Iminente',
	0,
	'© Bruno Cunha | CML | DPC | 2019',
	'Panorâmico de Monsanto',
	'38.728639, -9.184618',
	'existente'
);
INSERT INTO street_arts (sta_usr_id, sta_artist, sta_project, sta_year, sta_photo_credits, sta_address, sta_coords, sta_status)
  VALUES (
    1,
	'Oze Arv',
	'Rostos do Muro Azul',
	2012,
	'© CML | DMC | DPC | José Vicente 2012',
	'Rua das Murtas',
	'38.760555, -9.148055',
	'existente'
);

-- images

INSERT INTO images (img_usr_id, img_sta_id, img_url)
  VALUES (
	1,
	1,
	'http://gau.cm-lisboa.pt/fileadmin/templates/gau/app_v2/img.php?ficheiro_id=5437'
);
INSERT INTO images (img_usr_id, img_sta_id, img_url)
  VALUES (
	1,
	2,
	'http://gau.cm-lisboa.pt/fileadmin/templates/gau/app_v2/img.php?ficheiro_id=47125'
);
INSERT INTO images (img_usr_id, img_sta_id, img_url)
  VALUES (
	1,
	2,
	'http://gau.cm-lisboa.pt/fileadmin/templates/gau/app_v2/img.php?ficheiro_id=47126'
);
INSERT INTO images (img_usr_id, img_sta_id, img_url)
  VALUES (
	1,
	3,
	'http://gau.cm-lisboa.pt/fileadmin/templates/gau/app_v2/img.php?ficheiro_id=5440'
);

END;
