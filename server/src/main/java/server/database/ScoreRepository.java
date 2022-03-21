package server.database;

import org.springframework.data.jpa.repository.JpaRepository;

import commons.Score;

public interface ScoreRepository extends JpaRepository<Score, Long> {}
