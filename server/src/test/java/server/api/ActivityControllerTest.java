package server.api;

import commons.Activity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class ActivityControllerTest {

    public int nextInt;
    private MyRandom random;
    private TestActivityRepository repo;

    private ActivityController sut;

    @BeforeEach
    public void setup(){
        random = new MyRandom();
        repo = new TestActivityRepository();

        sut = new ActivityController(random, repo);
    }

    @Test
    public void testGetAll(){
        List<Activity> expected = new ArrayList<>();
        expected.add(getActivity("q1"));
        expected.add(getActivity("q2"));

        sut.addActivity(expected.get(0));
        sut.addActivity(expected.get(1));

        var actual = sut.getAll();
        assertEquals(expected, actual);

        List<String> expectedOperations = List.of("save", "save", "findAll");
        assertEquals(expectedOperations, repo.calledMethods);
    }

    @Test
    public void testGetRandom(){
        Activity expected = getActivity("q1");

        sut.addActivity(getActivity("q9"));
        sut.addActivity(expected);
        sut.addActivity(getActivity("q5"));

        nextInt = 1;
        repo.calledMethods.clear();
        Activity actual = sut.getRandom().getBody();

        assertEquals(expected, actual);
        assertTrue(random.wasCalled);
        assertEquals(List.of("count", "findAll"), repo.calledMethods);
    }

    @Test
    public void testGetSpecificActivity(){
        Activity expected = getActivity("q1");
        expected.id = 12L;

        Activity other = getActivity("q42");
        other.id = 6L;

        sut.addActivity(expected);
        sut.addActivity(other);
        repo.calledMethods.clear();

        assertEquals(expected, sut.getSpecificActivity(12L).getBody());
        assertEquals(List.of("findById"), repo.calledMethods);
    }

    @Test
    public void testGetSpecificActivityNotFound(){
        Activity q1 = getActivity("q1");
        q1.id = 12L;
        Activity q2 = getActivity("q42");
        q2.id = 6L;
        sut.addActivity(q1);
        sut.addActivity(q2);
        repo.calledMethods.clear();

        assertNull(sut.getSpecificActivity(9L).getBody());
        assertEquals(List.of("findById"), repo.calledMethods);
    }

    @Test
    public void testAddActivity(){
        Activity q1 = new Activity("Title to test", 420, "source::link", "image::path");
        q1.id = 11L;
        sut.addActivity(q1);
        assertEquals(List.of("save"), repo.calledMethods);

        Activity actual = sut.getSpecificActivity(11L   ).getBody();

        assertNotNull(actual);
        assertEquals( 11L, actual.id);
        assertEquals( "Title to test", actual.title);
        assertEquals( 420, actual.consumptionInWh);
        assertEquals("source::link", actual.source);
        assertEquals("image::path", actual.imagePath);

    }

    @Test
    public void testEditActivity(){
        Activity q1 = new Activity("Old title", 420, "source1", "image1");
        Activity q2 = new Activity("New title", 420, "source2", "image2");
        q1.id = 11L;
        q2.id = 12L;
        sut.addActivity(q1);
        sut.addActivity(q2);

        sut.editActivity(q1.id, q2);

        assertNotEquals(q1.id, q2.id);
        assertEquals(q1.title, q2.title);
        assertEquals(q1.consumptionInWh, q2.consumptionInWh);
        assertEquals(q1.source, q2.source);
        assertEquals(q1.imagePath, q2.imagePath);
    }

    @Test
    public void testDeleteActivity(){
        Activity expected = getActivity("qqq2");
        expected.id = 11L;
        sut.addActivity(expected);

        Activity actual = sut.deleteActivity(11L).getBody();

        assertEquals(expected, actual);
        assertNull(sut.getSpecificActivity(11L).getBody());
    }

    private static Activity getActivity(String data){
        return new Activity(data, 420, data, data);
    }

    public class MyRandom extends Random {

        public boolean wasCalled = false;

        @Override
        public int nextInt(int bound) {
            wasCalled = true;
            return nextInt;
        }
    }
}
