package ru.inside.commands.service.helper;

//import com.lowagie.text.pdf.BaseFont;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import ru.inside.commands.entity.Employee;
import ru.inside.commands.entity.PPE;
import ru.inside.commands.entity.forms.EmployeeForm;
import ru.inside.commands.entity.forms.PPEForm;

//import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class PdfGenerator {
    private final TemplateEngine templateEngine;

    public File createPdf(Employee employee, List<PPE> ppeList, String toSubsidiaryName) throws Exception {
        String templateName = "pdfTransferHtml";
        String fileName = "transfer_" + employee.getPersonnelNumber() + "_to_" + toSubsidiaryName + "_gen";

        EmployeeForm employeeForm = new EmployeeForm();
        employeeForm.setPersonnelNumber(employee.getPersonnelNumber());
        employeeForm.setOccupation(employee.getOccupation());
        employeeForm.setEmployeeName(employee.getEmployeeName());

        List<PPEForm> ppeFormList = new ArrayList<>();
        ppeList.forEach(ppe -> {
            ppeFormList.add(FormConverter.getPPEAsForm(ppe));
        });

        Context ctx = new Context();
        ctx.setVariable("employee", employeeForm);
        ctx.setVariable("ppeList", ppeFormList);
        ctx.setVariable("subsidiaryName", toSubsidiaryName);

        String processedHtml = templateEngine.process(templateName, ctx);
        FileOutputStream os = null;
        final File outputFile;

        try {
             outputFile = File.createTempFile(fileName, ".pdf",
                    new File(System.getProperty("user.dir")));
            os = new FileOutputStream(outputFile);

//            ITextRenderer renderer = new ITextRenderer();
//            renderer.setDocumentFromString(processedHtml);
//            try {
//                renderer.getFontResolver().addFont("/fonts/Calibri.ttf", BaseFont.IDENTITY_H, true);
//            } catch (Exception ex) {
//                log.warn("Cannot set font");
//            }
//            renderer.layout();
//            renderer.createPDF(os, false);
//            renderer.finishPDF();
        }
        finally {
            log.info("File generated as pdf");
            if (os != null) {
                try {
                    os.close();
                } catch (IOException ignored) {
                }
            }
        }
        return outputFile;
    }

    public File generateTransferDocument(Employee employee, String toSubsidiaryName) {
        log.info("generate pdf");
        List<PPE> employeePPEListToTransfer = employee.getPpe();
        try {
            return createPdf(employee, employeePPEListToTransfer, toSubsidiaryName);
        } catch (Exception e) {
            log.error("Cannot generate pdf transfer document");
        }
        return null;
    }
}
