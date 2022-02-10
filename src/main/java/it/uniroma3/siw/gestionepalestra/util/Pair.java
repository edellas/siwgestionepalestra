package it.uniroma3.siw.gestionepalestra.util;

import lombok.*;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Pair<K, V> {
    @Getter
    @Setter
    private K key;
    @Getter @Setter private V value;

}
