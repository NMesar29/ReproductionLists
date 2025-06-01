CREATE TABLE cancion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    artista VARCHAR(255) NOT NULL,
    album VARCHAR(255),
    anno INT,
    genero VARCHAR(100),
    CONSTRAINT uc_titulo_artista UNIQUE(titulo, artista)
);

CREATE TABLE lista (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    slug VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE lista_canciones (
    lista_id INT NOT NULL,
    canciones_id BIGINT NOT NULL,
    PRIMARY KEY (lista_id, canciones_id),
    FOREIGN KEY (lista_id) REFERENCES lista(id),
    FOREIGN KEY (canciones_id) REFERENCES cancion(id)
);