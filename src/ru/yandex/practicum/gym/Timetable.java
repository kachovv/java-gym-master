package ru.yandex.practicum.gym;

import java.util.*;

public class Timetable {

    private Map<DayOfWeek, TreeMap<TimeOfDay, TrainingSession>> timetable = new HashMap<>();

    public void addNewTrainingSession(TrainingSession trainingSession) {
        DayOfWeek day = trainingSession.getDayOfWeek();
        TimeOfDay time = trainingSession.getTimeOfDay();

        TreeMap<TimeOfDay, TrainingSession> dayTimeTable = timetable.get(day);

        if (dayTimeTable == null) {
            dayTimeTable = new TreeMap<>();
            timetable.put(day, dayTimeTable);
        }
        dayTimeTable.put(time, trainingSession);
    }

    public SortedMap<TimeOfDay, TrainingSession> getTrainingSessionsForDay(DayOfWeek dayOfWeek) {
        TreeMap<TimeOfDay, TrainingSession> dayTimeTable = timetable.get(dayOfWeek);

        if (dayTimeTable == null) {
            return Collections.emptySortedMap();
        }
        return Collections.unmodifiableSortedMap(dayTimeTable);
    }

    public TrainingSession getTrainingSessionsForDayAndTime(DayOfWeek dayOfWeek, TimeOfDay timeOfDay) {
        TreeMap<TimeOfDay, TrainingSession> dayTimeTable = timetable.get(dayOfWeek);

        if (dayTimeTable == null) {
            return null;
        }
        return dayTimeTable.get(timeOfDay);
    }

    //Метод для считывания тренировок
    public List<CounterOfTrainings> getCountByCoaches() {
        Map<Coach, Integer> countMap = new HashMap();

        for (TreeMap<TimeOfDay, TrainingSession> dayTimetable : timetable.values()) {
            for (TrainingSession session : dayTimetable.values()) {
                Coach coach = session.getCoach();

                if (countMap.containsKey(coach)) {
                    countMap.put(coach, countMap.get(coach) + 1);
                } else {
                    countMap.put(coach, 1);
                }
            }
        }

            List<CounterOfTrainings> result = new ArrayList<>();

            for (Map.Entry<Coach, Integer> entry : countMap.entrySet()) {
                result.add(new CounterOfTrainings(entry.getKey(), entry.getValue()));
                }
                result.sort((a, b) -> Integer.compare(b.getCount(), a.getCount()));

                return result;
            }
        }