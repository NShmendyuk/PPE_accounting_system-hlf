package ru.inside.commands.entity.forms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.inside.commands.entity.enums.SubsidiaryStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubsidiaryForm {
    String name;
    String peerName;
    SubsidiaryStatus status;
}
