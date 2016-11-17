package ru.klinichev.client.mvp;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

import ru.klinichev.client.place.ChatPlace;

@WithTokenizers( { ChatPlace.Tokenizer.class })
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper {

}
