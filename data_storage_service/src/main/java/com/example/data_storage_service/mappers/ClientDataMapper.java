package com.example.data_storage_service.mappers;

import com.example.data_storage_service.dtos.ClientDataDTO;
import com.example.data_storage_service.dtos.SavedContactsDTO;
import com.example.data_storage_service.models.ClientData;
import com.example.data_storage_service.models.SavedContact;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ClientDataMapper {

    @Mapping(target = "cardNumbers", source = "cardNumbers")
    @Mapping(target = "savedContacts", source = "savedContacts")
    ClientData toClientData(ClientDataDTO clientDataDTO);

    default List<SavedContact> mapSavedContactsDtoToListSavedContact(List<SavedContactsDTO> dtos) {
        if (dtos == null) {
            return new ArrayList<>();
        }
        return dtos.stream()
                .map(dto -> new SavedContact(dto.getContactName(), dto.getContactNumber()))
                .collect(Collectors.toList());
    }

    default List<SavedContactsDTO> mapListSavedContactToSavedContactsDto(List<SavedContact> contacts) {
        if (contacts == null) {
            return new ArrayList<>();
        }
        return contacts.stream()
                .map(contact -> new SavedContactsDTO(contact.getContactName(), contact.getContactNumber()))
                .collect(Collectors.toList());
    }

    default List<String> mapCardNumbers(List<String> cardNumbers) {
        return cardNumbers == null ? new ArrayList<>() : new ArrayList<>(cardNumbers);
    }
}
