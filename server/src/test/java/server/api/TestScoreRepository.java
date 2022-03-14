package server.api;

import commons.Score;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import server.database.ScoreRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TestScoreRepository implements ScoreRepository {
    public final List<Score> scores = new ArrayList<>();
    public final List<String> calledMethods = new ArrayList<>();

    private void call(String name) {calledMethods.add(name);}

    @Override
    public List<Score> findAll() {
        call("findAll");
        return scores;
    }

    @Override
    public List<Score> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<Score> findAll(Pageable pageable) {
        call("findAll");

        List<Score> toReturn = new ArrayList<>();
        toReturn.add(scores.get(pageable.getPageNumber()));
        return new PageImpl<Score>(toReturn);
    }

    @Override
    public List<Score> findAllById(Iterable<Long> longs) {
        return null;
    }

    @Override
    public long count() {
        call("count");
        return scores.size();
    }

    @Override
    public void deleteById(Long aLong) {
        call("deleteById");

        var found = scores.stream().filter(q -> q.id == aLong).collect(Collectors.toList());
        if(found.size() > 0) scores.remove(found.get(0));

    }

    @Override
    public void delete(Score entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends Score> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public <S extends Score> S save(S entity) {
        call("save");
        scores.add(entity);
        return null;
    }

    @Override
    public <S extends Score> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<Score> findById(Long aLong) {
        call("findById");

        for(var q : scores){
            if(q.id == aLong) return Optional.of(q);
        }
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends Score> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Score> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<Score> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Score getOne(Long aLong) {
        return null;
    }

    @Override
    public Score getById(Long aLong) {
        return null;
    }

    @Override
    public <S extends Score> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Score> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends Score> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends Score> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Score> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Score> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Score, R> R findBy(Example<S> example,
                                         Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

}
