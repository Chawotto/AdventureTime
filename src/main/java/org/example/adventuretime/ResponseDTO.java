package org.example.adventuretime;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResponseDto {
    private String message;

    public ResponseDto(String message) {
        this.message = message;
    }

}
