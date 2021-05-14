package ru.inside.commands.service.helper;

import lombok.experimental.UtilityClass;
import ru.inside.commands.entity.PPE;
import ru.inside.commands.entity.forms.PPEForm;

@UtilityClass
public class FormConverter {
    public PPEForm getPPEAsForm(PPE ppe) {
        PPEForm ppeForm = new PPEForm();
        ppeForm.setPpeName(ppe.getName());
        ppeForm.setPrice(ppe.getPrice());
        ppeForm.setPpeStatus(ppe.getPpeStatus().toString());
        ppeForm.setInventoryNumber(ppe.getInventoryNumber());
        ppeForm.setStartUseDate(ppe.getStartUseDate());
        ppeForm.setLifeTime(ppe.getLifeTime());
        if (ppe.getEmployee() != null) {
            ppeForm.setOwnerName(ppe.getEmployee().getEmployeeName());
            ppeForm.setOwnerPersonnelNumber(ppe.getEmployee().getPersonnelNumber().toString());
            ppeForm.setSubsidiaryName(ppe.getEmployee().getSubsidiary().getName());
        }
        return ppeForm;
    }

}
