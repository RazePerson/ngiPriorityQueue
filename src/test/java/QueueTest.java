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

    @RepeatedTest(100)
    public void pop_100Threads100Times_sizeMatches() throws ExecutionException, InterruptedException {
        //given
        MyPriorityQueue<Patient, State> myPriorityQueue = new MyPriorityQueueImpl<>();
        Patient pat1 = new Patient("Pat1");
        Patient pat2 = new Patient("Pat2");
        Patient pat3 = new Patient("Pat3");
        Patient pat4 = new Patient("Pat4");
        Patient pat5 = new Patient("Pat5");
        Patient pat6 = new Patient("Pat6");
        myPriorityQueue.add(pat1, new State("Broken leg", 10));
        myPriorityQueue.add(pat2, new State("Sore throat", 20));
        myPriorityQueue.add(pat3, new State("Half dead", 1));
        myPriorityQueue.add(pat4, new State("Broken leg", 10));
        myPriorityQueue.add(pat5, new State("Heart attack", 5));
        myPriorityQueue.add(pat6, new State("Sore throat", 20));

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
        try {
            service.awaitTermination(100, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

    @RepeatedTest(100)
    public void add_100Threads100Times_everyPatientIsAdded() {
        //given
        MyPriorityQueue<Patient, State> myPriorityQueue = new MyPriorityQueueImpl<>();
        List<State> states = new ArrayList<>();
        states.add(new State("Broken leg", 10));
        states.add(new State("Sore throat", 20));
        states.add(new State("Half dead", 1));
        states.add(new State("Broken arm", 15));
        states.add(new State("Heart attack", 5));
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
            service.awaitTermination(100, TimeUnit.MILLISECONDS);
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
        myPriorityQueue.add(pat1, new State("Sore throat", 20));
        myPriorityQueue.add(pat2, new State("Heart attack", 5));
        Patient pop1 = myPriorityQueue.pop();
        myPriorityQueue.add(pat3, new State("Half dead", 1));
        Patient pop2 = myPriorityQueue.pop();
        myPriorityQueue.add(pat4, new State("Broken leg", 10));
        myPriorityQueue.update(pat1, new State("Heart attack", 5));
        Patient pop3 = myPriorityQueue.pop();
        myPriorityQueue.add(pat5, new State("Sore throat", 20));
        myPriorityQueue.update(pat4, new State("Broken arm", 15));
        myPriorityQueue.add(pat6, new State("Half dead", 1));
        Patient pop4 = myPriorityQueue.pop();
        Patient pop5 = myPriorityQueue.pop();
        Patient pop6 = myPriorityQueue.pop();

        //then order is pat2, pat3, pat1, pat6, pat4, pat5
        assertEquals(pat1, pop3);
        assertEquals(pat2, pop1);
        assertEquals(pat3, pop2);
        assertEquals(pat4, pop5);
        assertEquals(pat5, pop6);
        assertEquals(pat6, pop4);
    }

    @Test
    public void priorityQueue_addPatient_poppedPatientIsTheSame() {
        //given
        MyPriorityQueue<Patient, State> myPriorityQueue = new MyPriorityQueueImpl<>();
        Patient patient = new Patient("Patient");
        myPriorityQueue.add(patient, new State("Broken leg", 10));

        //when
        Patient poppedPatient = myPriorityQueue.pop();

        //then
        assertEquals(patient, poppedPatient);
    }

    @Test
    public void priorityQueue_addPatientTwice_poppedOnce() {
        //given
        MyPriorityQueue<Patient, State> myPriorityQueue = new MyPriorityQueueImpl<>();
        Patient patient = new Patient("Patient");
        myPriorityQueue.add(patient, new State("Broken leg", 10));
        myPriorityQueue.add(patient, new State("Broken leg", 10));

        //when
        Patient poppedPatient1 = myPriorityQueue.pop();
        Patient poppedPatient2 = myPriorityQueue.pop();

        //then
        assertEquals(patient, poppedPatient1);
        assertNull(poppedPatient2);
    }

    @Test
    public void priorityQueue_addPatientTwiceDifferentState_poppedOnce() {
        //given
        MyPriorityQueue<Patient, State> myPriorityQueue = new MyPriorityQueueImpl<>();
        Patient patient = new Patient("Patient");
        myPriorityQueue.add(patient, new State("Broken leg", 10));
        myPriorityQueue.add(patient, new State("Heart attack", 5));

        //when
        Patient poppedPatient1 = myPriorityQueue.pop();
        Patient poppedPatient2 = myPriorityQueue.pop();

        //then
        assertEquals(patient, poppedPatient1);
        assertNull(poppedPatient2);
    }

    @Test
    public void priorityQueue_update_patientStateUpdated() {
        //given
        MyPriorityQueue<Patient, State> myPriorityQueue = new MyPriorityQueueImpl<>();
        Patient patient1 = new Patient("Patient1");
        Patient patient2 = new Patient("Patient2");
        myPriorityQueue.add(patient1, new State("Broken leg", 10));
        myPriorityQueue.add(patient2, new State("Heart attack", 5));

        //when
        myPriorityQueue.update(patient1, new State("Something more severe than heart attack", 1));
        Patient poppedPatient = myPriorityQueue.pop();

        //then patient1 gets popped instead of patient2 because of the update
        assertEquals(patient1.getName(), poppedPatient.getName());
    }

    @Test
    public void priorityQueue_noPatients_poppedPatientIsNull() {
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
        myPriorityQueue.add(pat1, new State("Heart attack", 5));
        myPriorityQueue.add(pat2, new State("Heart attack", 5));
        myPriorityQueue.add(pat3, new State("Half dead", 1));
        myPriorityQueue.add(pat4, new State("Sore throat", 20));
        myPriorityQueue.add(pat5, new State("Sore throat", 20));
        myPriorityQueue.add(pat6, new State("Half dead", 1));
        Patient pop1 = myPriorityQueue.pop();
        Patient pop2 = myPriorityQueue.pop();
        Patient pop3 = myPriorityQueue.pop();
        Patient pop4 = myPriorityQueue.pop();
        Patient pop5 = myPriorityQueue.pop();
        Patient pop6 = myPriorityQueue.pop();

        //then order is pat3, pat6, pat1, pat2, pat4, pat5
        assertEquals(pat1, pop3);
        assertEquals(pat2, pop4);
        assertEquals(pat6, pop2);
        assertEquals(pat5, pop6);
        assertEquals(pat4, pop5);
        assertEquals(pat3, pop1);
    }
}
