package ru.yandex.practicum.gym;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

public class TimetableTest {
    private Timetable timetable;

    @BeforeEach
    void setUp() {
        timetable = new Timetable();
    }

    @Test
    void testGetTrainingSessionsForDaySingleSession() {
        Group group = new Group("Акробатика для детей", Age.CHILD, 60);
        Coach coach = new Coach("Васильев", "Николай", "Сергеевич");
        TrainingSession singleTrainingSession = new TrainingSession(group, coach,
                DayOfWeek.MONDAY, new TimeOfDay(13, 0));

        timetable.addNewTrainingSession(singleTrainingSession);

        //Проверить, что за понедельник вернулось одно занятие
        SortedMap<TimeOfDay, TrainingSession> mondaySessions = timetable.getTrainingSessionsForDay((DayOfWeek.MONDAY));
        assertNotNull(mondaySessions);
        assertEquals(1, mondaySessions.size());
        assertTrue(mondaySessions.containsKey(new TimeOfDay(13, 0)));
        //Проверить, что за вторник не вернулось занятий
        SortedMap<TimeOfDay, TrainingSession> tuesdaySessions = timetable.getTrainingSessionsForDay(DayOfWeek.TUESDAY);
        assertNotNull(tuesdaySessions);
        assertTrue(tuesdaySessions.isEmpty());
    }

    @Test
    void testGetTrainingSessionsForDayMultipleSessions() {
        Coach coach = new Coach("Васильев", "Николай", "Сергеевич");

        Group groupAdult = new Group("Акробатика для взрослых", Age.ADULT, 90);
        TrainingSession thursdayAdultTrainingSession = new TrainingSession(groupAdult, coach,
                DayOfWeek.THURSDAY, new TimeOfDay(20, 0));

        timetable.addNewTrainingSession(thursdayAdultTrainingSession);

        Group groupChild = new Group("Акробатика для детей", Age.CHILD, 60);
        TrainingSession mondayChildTrainingSession = new TrainingSession(groupChild, coach,
                DayOfWeek.MONDAY, new TimeOfDay(13, 0));
        TrainingSession thursdayChildTrainingSession = new TrainingSession(groupChild, coach,
                DayOfWeek.THURSDAY, new TimeOfDay(13, 0));
        TrainingSession saturdayChildTrainingSession = new TrainingSession(groupChild, coach,
                DayOfWeek.SATURDAY, new TimeOfDay(10, 0));

        timetable.addNewTrainingSession(mondayChildTrainingSession);
        timetable.addNewTrainingSession(thursdayChildTrainingSession);
        timetable.addNewTrainingSession(saturdayChildTrainingSession);

        // Проверить, что за понедельник вернулось одно занятие
        SortedMap<TimeOfDay, TrainingSession> mondaySessions = timetable.getTrainingSessionsForDay(DayOfWeek.MONDAY);
        assertEquals(1, mondaySessions.size());
        assertTrue(mondaySessions.containsKey(new TimeOfDay(13, 0)));

        // Проверить, что за четверг вернулось два занятия в правильном порядке: сначала в 13:00, потом в 20:00
        SortedMap<TimeOfDay, TrainingSession> thursdaySessions = timetable.getTrainingSessionsForDay(DayOfWeek.THURSDAY);
        assertEquals(2, thursdaySessions.size());
        List<TimeOfDay> times = new ArrayList<>(thursdaySessions.keySet());
        assertEquals(new TimeOfDay(13, 0), times.get(0));
        assertEquals(new TimeOfDay(20, 0), times.get(1));

        // Проверить, что за вторник не вернулось занятий
        SortedMap<TimeOfDay, TrainingSession> tuesdaySessions = timetable.getTrainingSessionsForDay(DayOfWeek.TUESDAY);
        assertTrue(tuesdaySessions.isEmpty());
    }

    @Test
    void testGetTrainingSessionsForDayAndTime() {
        Group group = new Group("Акробатика для детей", Age.CHILD, 60);
        Coach coach = new Coach("Васильев", "Николай", "Сергеевич");
        TrainingSession singleTrainingSession = new TrainingSession(group, coach,
                DayOfWeek.MONDAY, new TimeOfDay(13, 0));

        timetable.addNewTrainingSession(singleTrainingSession);

        //Проверить, что за понедельник в 13:00 вернулось одно занятие
        TrainingSession sessionFound = timetable.getTrainingSessionsForDayAndTime(DayOfWeek.MONDAY, new TimeOfDay(13, 0));
        assertNotNull(sessionFound);
        assertSame(singleTrainingSession, sessionFound);

        //Проверить, что за понедельник в 14:00 не вернулось занятий
        TrainingSession sessionNotFound = timetable.getTrainingSessionsForDayAndTime(DayOfWeek.MONDAY, new TimeOfDay(14, 0));
        assertNull(sessionNotFound);
    }

    //Тест на добавление нескольких тренировок в один день с проверкой на порядок
    @Test
    void testGetTrainingSessionsForDayWithMultipleSessionsOrder() {
        Coach coach = new Coach("Куркин", "Иван", "Анатольевич");
        Group group = new Group("Пилатес", Age.ADULT, 75);

        TrainingSession session1 = new TrainingSession(group, coach, DayOfWeek.WEDNESDAY, new TimeOfDay(14, 0));
        TrainingSession session2 = new TrainingSession(group, coach, DayOfWeek.WEDNESDAY, new TimeOfDay(9, 0));
        TrainingSession session3 = new TrainingSession(group, coach, DayOfWeek.WEDNESDAY, new TimeOfDay(17, 30));

        timetable.addNewTrainingSession(session1);
        timetable.addNewTrainingSession(session2);
        timetable.addNewTrainingSession(session3);

        SortedMap<TimeOfDay, TrainingSession> wednesdaySessions = timetable.getTrainingSessionsForDay(DayOfWeek.WEDNESDAY);
        assertEquals(3, wednesdaySessions.size());

        List<TimeOfDay> expectedTimes = Arrays.asList(
                new TimeOfDay(9, 0),
                new TimeOfDay(14, 0),
                new TimeOfDay(17, 30)
                );
        assertEquals(expectedTimes, new ArrayList<>(wednesdaySessions.keySet()));
        assertSame(session2, wednesdaySessions.get(new TimeOfDay(9, 0)));
        assertSame(session1, wednesdaySessions.get(new TimeOfDay(14, 0)));
        assertSame(session3, wednesdaySessions.get(new TimeOfDay(17, 30)));
    }

    //Тест на вывод отсутствующей тренировки
    @Test
    void testGetTrainingSessionForDayAndTimeForNonExistentDay() {
        TrainingSession result = timetable.getTrainingSessionsForDayAndTime(DayOfWeek.SUNDAY, new TimeOfDay(10, 0));
        assertNull(result);
    }

    //Тест на перезапись тренировки
    @Test
    void testAddNewTrainingSessionReplacesExisting() {
        Coach coach1 = new Coach("Морозова", "Алина", "Сергеевна");
        Group group1 = new Group("Йога", Age.ADULT, 60);
        TrainingSession trainingSession1 = new TrainingSession(group1, coach1, DayOfWeek.FRIDAY, new TimeOfDay(10, 0));

        timetable.addNewTrainingSession(trainingSession1);

        Coach coach2 = new Coach("Артемова", "Евгения", "Александровна");
        Group group2 = new Group("Силовая тренировка", Age.ADULT, 60);
        TrainingSession trainingSession2 = new TrainingSession(group2, coach2, DayOfWeek.FRIDAY, new TimeOfDay(10, 0));

        timetable.addNewTrainingSession(trainingSession2);

        SortedMap<TimeOfDay, TrainingSession> fridaySessions = timetable.getTrainingSessionsForDay(DayOfWeek.FRIDAY);
        assertEquals(1, fridaySessions.size());
        assertSame(trainingSession2, fridaySessions.get(new TimeOfDay(10, 0)));

        TrainingSession sessionFound = timetable.getTrainingSessionsForDayAndTime(DayOfWeek.FRIDAY, new TimeOfDay(10, 0));
        assertSame(trainingSession2, sessionFound);
    }

    //Тест на вызов пустого расписания
    @Test
    void testGetCountByCoachesEmptyTimetable() {
        List<CounterOfTrainings> result = timetable.getCountByCoaches();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    //Тест на добавление нескольких тренеров с разным количеством занятий
    @Test
    void testGetCountByCoachesWithMultipleCoaches() {
        Coach coachA = new Coach("Печенкин", "Иван", "Иванович");
        Coach coachB = new Coach("Толстой", "Петр", "Петрович");
        Coach coachC = new Coach("Перепелкин", "Сидор", "Сидорович");
        Group group = new Group("Гимнастика", Age.ADULT, 60);

        timetable.addNewTrainingSession(new TrainingSession(group, coachA, DayOfWeek.MONDAY, new TimeOfDay(9, 0)));
        timetable.addNewTrainingSession(new TrainingSession(group, coachA, DayOfWeek.WEDNESDAY, new TimeOfDay(9, 0)));
        timetable.addNewTrainingSession(new TrainingSession(group, coachA, DayOfWeek.FRIDAY, new TimeOfDay(9, 0)));

        timetable.addNewTrainingSession(new TrainingSession(group, coachB, DayOfWeek.TUESDAY, new TimeOfDay(10, 0)));

        timetable.addNewTrainingSession(new TrainingSession(group, coachC, DayOfWeek.THURSDAY, new TimeOfDay(11, 0)));
        timetable.addNewTrainingSession(new TrainingSession(group, coachC, DayOfWeek.THURSDAY, new TimeOfDay(14, 0)));

        List<CounterOfTrainings> result = timetable.getCountByCoaches();

        assertEquals(3, result.size());
        assertEquals(result.get(0).getCoach(), coachA);
        assertEquals(3, result.get(0).getCount());
        assertEquals(result.get(1).getCoach(), coachC);
        assertEquals(2, result.get(1).getCount());
        assertEquals(result.get(2).getCoach(), coachB);
        assertEquals(1, result.get(2).getCount());
    }

    //Тест на добавление одного тренера с несколькими тренировками
    @Test
    void testGetCountByCoachesWithSingleCoach() {
        Coach coach = new Coach("Артемова","Ирина", "Олеговна");
        Group group = new Group("Пилатес", Age.ADULT, 60);

        timetable.addNewTrainingSession(new TrainingSession(group, coach, DayOfWeek.MONDAY, new TimeOfDay(8, 0)));
        timetable.addNewTrainingSession(new TrainingSession(group, coach, DayOfWeek.WEDNESDAY, new TimeOfDay(8, 0)));
        timetable.addNewTrainingSession(new TrainingSession(group, coach, DayOfWeek.FRIDAY, new TimeOfDay(8, 0)));

        List<CounterOfTrainings> result = timetable.getCountByCoaches();

        assertEquals(1, result.size());
        assertEquals(result.getFirst().getCoach(), coach);
        assertEquals(3, result.getFirst().getCount());
    }
}
