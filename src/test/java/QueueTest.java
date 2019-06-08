import data.Patient;
import data.State;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import queue.MyPriorityQueue;
import queue.impl.MyPriorityQueueImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;


public class QueueTest {

    @Test
    @RepeatedTest(100)
    public void pop_1000Threads100Times_sizeMatches() throws ExecutionException, InterruptedException {
        //given
        MyPriorityQueue<Patient, State> myPriorityQueue = new MyPriorityQueueImpl<>();
        Patient pat1 = new Patient("Pat1");
        Patient pat2 = new Patient("Pat2");
        Patient pat3 = new Patient("Pat3");
        Patient pat4 = new Patient("Pat4");
        Patient pat5 = new Patient("Pat5");
        Patient pat6 = new Patient("Pat6");
        myPriorityQueue.add(pat1, State.BROKEN_LEG);
        myPriorityQueue.add(pat2, State.SORE_THROAT);
        myPriorityQueue.add(pat3, State.HALF_DEAD);
        myPriorityQueue.add(pat4, State.BROKEN_LEG);
        myPriorityQueue.add(pat5, State.HEART_ATTACK);
        myPriorityQueue.add(pat6, State.SORE_THROAT);

        int threads = 100;
        ExecutorService service =
                Executors.newFixedThreadPool(threads);
        Collection<Future<Patient>> futures =
                new ArrayList<>(threads);

        //when
        for (int i = 0; i < threads; i++) {
            futures.add(service.submit(myPriorityQueue::pop));
        }
        service.shutdown();
        List<Patient> patients = new ArrayList<>();
        for (Future<Patient> f : futures) {
            Patient patient = f.get();
            if (patient != null) {
                patients.add(patient);
            }
        }

        //then
        assertEquals(6, patients.size());
    }

    @Test
    @RepeatedTest(100)
    public void add_100Threads100Times_everyPatientIsAdded() {
        //given
        MyPriorityQueue<Patient, State> myPriorityQueue = new MyPriorityQueueImpl<>();
        List<State> states = List.of(State.values());
        int stateSize = states.size();
        Random random = new Random();
        int threads = 100;
        ExecutorService service =
                Executors.newFixedThreadPool(threads);
        List<Patient> patientsOriginal = new ArrayList<>();

        //when
        for (int i = 0; i < threads; i++) {
            String patientName = UUID.randomUUID().toString();
            State state = states.get(random.nextInt(stateSize));
            Patient patient = new Patient(patientName);
            patientsOriginal.add(patient);
            service.execute(() -> myPriorityQueue.add(patient, state));
        }
        service.shutdown();
        try {
            service.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Patient patient;
        List<Patient> patients = new ArrayList<>();
        while ((patient = myPriorityQueue.pop()) != null) {
            patients.add(patient);
        }

        //then
        assertEquals(patientsOriginal.size(), patients.size());
        assertFalse(patients.stream()
                .anyMatch(patient1 -> !patientsOriginal.contains(patient1)));
    }

    @Test
    public void priorityQueue_6Patients_orderIsCorrect() {
        //given
        MyPriorityQueue<Patient, State> myPriorityQueue = new MyPriorityQueueImpl<>();
        Patient pat1 = new Patient("Pat1");
        Patient pat2 = new Patient("Pat2");
        Patient pat3 = new Patient("Pat3");
        Patient pat4 = new Patient("Pat4");
        Patient pat5 = new Patient("Pat5");
        Patient pat6 = new Patient("Pat6");

        //when
        myPriorityQueue.add(pat1, State.SORE_THROAT);
        myPriorityQueue.add(pat2, State.HEART_ATTACK);
        Patient pop1 = myPriorityQueue.pop();
        myPriorityQueue.add(pat3, State.HALF_DEAD);
        Patient pop2 = myPriorityQueue.pop();
        myPriorityQueue.add(pat4, State.BROKEN_LEG);
        myPriorityQueue.update(pat1, State.HEART_ATTACK);
        Patient pop3 = myPriorityQueue.pop();
        myPriorityQueue.add(pat5, State.SORE_THROAT);
        myPriorityQueue.update(pat4, State.BROKEN_ARM);
        myPriorityQueue.add(pat6, State.HALF_DEAD);
        Patient pop4 = myPriorityQueue.pop();
        Patient pop5 = myPriorityQueue.pop();
        Patient pop6 = myPriorityQueue.pop();

        //then
        assertEquals(pat1.getName(), pop3.getName());
        assertEquals(pat2.getName(), pop1.getName());
        assertEquals(pat3.getName(), pop2.getName());
        assertEquals(pat4.getName(), pop5.getName());
        assertEquals(pat5.getName(), pop6.getName());
        assertEquals(pat6.getName(), pop4.getName());
    }

    @Test
    public void priorityQueue_noPatients_expectedNull() {
        //given
        MyPriorityQueue<Patient, State> myPriorityQueue = new MyPriorityQueueImpl<>();

        //when
        Patient pop = myPriorityQueue.pop();

        //then
        assertNull(pop);
    }

    @Test
    public void priorityQueue_multiplePatientsSameState_orderIsCorrect() {
        //given
        MyPriorityQueue<Patient, State> myPriorityQueue = new MyPriorityQueueImpl<>();
        Patient pat1 = new Patient("Pat1");
        Patient pat2 = new Patient("Pat2");
        Patient pat3 = new Patient("Pat3");
        Patient pat4 = new Patient("Pat4");
        Patient pat5 = new Patient("Pat5");
        Patient pat6 = new Patient("Pat6");

        //when
        myPriorityQueue.add(pat1, State.HEART_ATTACK);
        myPriorityQueue.add(pat2, State.HEART_ATTACK);
        myPriorityQueue.add(pat3, State.HALF_DEAD);
        myPriorityQueue.add(pat4, State.SORE_THROAT);
        myPriorityQueue.add(pat5, State.SORE_THROAT);
        myPriorityQueue.add(pat6, State.HALF_DEAD);
        Patient pop1 = myPriorityQueue.pop();
        Patient pop2 = myPriorityQueue.pop();
        Patient pop4 = myPriorityQueue.pop();
        Patient pop3 = myPriorityQueue.pop();
        Patient pop5 = myPriorityQueue.pop();
        Patient pop6 = myPriorityQueue.pop();

        //then
        assertEquals(pat1.getName(), pop3.getName());
        assertEquals(pat2.getName(), pop4.getName());
        assertEquals(pat6.getName(), pop1.getName());
        assertEquals(pat5.getName(), pop5.getName());
        assertEquals(pat4.getName(), pop6.getName());
        assertEquals(pat3.getName(), pop2.getName());
    }
}
