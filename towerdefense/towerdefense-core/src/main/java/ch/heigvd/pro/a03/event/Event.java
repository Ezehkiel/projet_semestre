package ch.heigvd.pro.a03.event;

import ch.heigvd.pro.a03.event.player.PlayerEvent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

public abstract class Event {
    public static LinkedList<Event> getEvents(ObjectInputStream in){
        LinkedList<Event> events =null;
        try{
            events = (LinkedList<Event>) in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return  events;
    }
    public static Event getEvent(ObjectInputStream in) {
        Event event = null;
        try {
            event = (Event) in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return event;
    }
    public static void sendEvent(Event event, ObjectOutputStream out) {
        try {
            out.writeObject(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void sendEvents(LinkedList<Event> events, ObjectOutputStream out) {
        for (Event playerEvent : events)
            sendEvent(playerEvent,out);
    }
}
