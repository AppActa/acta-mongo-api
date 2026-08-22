package br.com.picpay.actamongoapi.document.base;

import java.util.UUID;

import org.springframework.data.annotation.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public abstract class BaseDocument {
    @Id
    private UUID id = UUID.randomUUID();
}
