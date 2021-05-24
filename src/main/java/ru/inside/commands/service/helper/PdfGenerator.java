package ru.inside.commands.service.helper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import ru.inside.commands.entity.Employee;
import ru.inside.commands.entity.PPE;
import ru.inside.commands.entity.forms.EmployeeForm;
import ru.inside.commands.entity.forms.PPEForm;

import com.lowagie.text.pdf.BaseFont;
import org.xhtmlrenderer.pdf.ITextRenderer;
import ru.inside.commands.hyperledger.ChainCodeControllerService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class PdfGenerator {
    private final TemplateEngine templateEngine;
    private final ChainCodeControllerService chainCodeControllerService;


    public File createPdfTransfer(Employee employee, List<PPE> ppeList, String toSubsidiaryName) throws Exception {
        String templateName = "pdfTransferHtml";
        String fileName = "transfer_" + employee.getPersonnelNumber().toLowerCase() + "_to_" + toSubsidiaryName.toLowerCase() + "_gen";

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

            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(processedHtml);
            try {
                renderer.getFontResolver().addFont(System.getProperty("user.dir") + "/src/main/resources/fonts/calibri.ttf",
                        "cp1251", BaseFont.EMBEDDED);
            } catch (Exception ex) {
                log.warn("Cannot set font");
            }
            renderer.layout();
            renderer.createPDF(os, false);
            renderer.finishPDF();
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
            return createPdfTransfer(employee, employeePPEListToTransfer, toSubsidiaryName);
        } catch (Exception e) {
            log.error("Cannot generate pdf transfer document");
        }
        return null;
    }


    public File generateAllApplyTransferDocument(List<PPEForm> waitAllPPE) {
        String fileName = "apply transfer all PPE generated ("  + LocalDateTime.now().toString() + ").pdf";
        List<PPEForm> ppeFormList = new ArrayList<>();
        List<EmployeeForm> employeeFormList = new ArrayList<>();

        waitAllPPE.forEach(ppeWait -> {
            PPEForm ppeForm = new PPEForm();
            EmployeeForm employeeForm = new EmployeeForm();

            employeeForm.setEmployeeName(ppeWait.getOwnerName());
            employeeForm.setPersonnelNumber(ppeWait.getOwnerPersonnelNumber());

            ppeForm.setOwnerPersonnelNumber(ppeWait.getOwnerPersonnelNumber());

            ppeForm.setSubsidiaryName("ОШИБКА!");
            try {
                String fromSubsidiary = chainCodeControllerService
                        .getPPEHistoryByInventoryNumber(ppeWait.getInventoryNumber())
                        .get(1).getSubsidiary();
                ppeForm.setSubsidiaryName(fromSubsidiary);
            } catch (Exception ex) {
                log.error("Cannot find subsidiary which transfered ppe {} to this subsidiary!!", ppeWait.getInventoryNumber());
            }

            ppeForm.setPrice(ppeWait.getPrice());
            ppeForm.setPpeStatus(ppeWait.getPpeStatus());
            ppeForm.setStartUseDate(ppeWait.getStartUseDate());
            ppeForm.setLifeTime(ppeWait.getLifeTime());

            ppeFormList.add(ppeForm);
            employeeFormList.add(employeeForm);
        });

        try {
            return generateApplyWithData(ppeFormList, employeeFormList, fileName);
        } catch (Exception e) {
            log.error("Cannot generate pdf file with data");
        }
        return null;
    }

    public File generateSingleApplyTransferDocument(PPE ppe, Employee employee) {
        String fileName = "apply PPE: (" + ppe.getInventoryNumber() +
                "; employee:" + employee.getPersonnelNumber()
                +"); generated(" + LocalDate.now().toString() + ").pdf";
        List<PPEForm> ppeFormList = new ArrayList<>();
        List<EmployeeForm> employeeFormList = new ArrayList<>();
        PPEForm ppeForm = new PPEForm();
        EmployeeForm employeeForm = new EmployeeForm();

        employeeForm.setPersonnelNumber(employee.getPersonnelNumber());
        employeeForm.setEmployeeName(employee.getEmployeeName());

        ppeForm.setLifeTime(ppe.getLifeTime());
        ppeForm.setStartUseDate(ppe.getStartUseDate());
        ppeForm.setPrice(ppe.getPrice());
        ppeForm.setPpeStatus(ppe.getPpeStatus().toString());
        ppeForm.setSubsidiaryName("ОШИБКА!");
        try {
            String fromSubsidiary = chainCodeControllerService
                    .getPPEHistoryByInventoryNumber(ppe.getInventoryNumber())
                    .get(1).getSubsidiary();
            ppeForm.setSubsidiaryName(fromSubsidiary);
        } catch (Exception ex) {
            log.error("Cannot find subsidiary which transfered ppe {} to this subsidiary!!", ppe.getInventoryNumber());
        }
        ppeForm.setOwnerPersonnelNumber(ppe.getEmployee().getPersonnelNumber());
        ppeForm.setInventoryNumber(ppe.getInventoryNumber());
        ppeForm.setPpeName(ppe.getName());

        ppeFormList.add(ppeForm);
        employeeFormList.add(employeeForm);
        try {
            return generateApplyWithData(ppeFormList, employeeFormList, fileName);
        } catch (Exception e) {
            log.error("Cannot generate document with single data for apply ppe {}", ppe.getInventoryNumber());
        }
        return null;
    }

    private File generateApplyWithData(List<PPEForm> ppeFormList, List<EmployeeForm> employeeFormList, String fileName) throws Exception {
        String templateName = "pdfApplyTransferHtml";

        Context ctx = new Context();
        ctx.setVariable("ppeList", ppeFormList);
        ctx.setVariable("employeeList", employeeFormList);
        String processedHtml = templateEngine.process(templateName, ctx);

        FileOutputStream os = null;
        final File outputFile;

        try {
            outputFile = File.createTempFile(fileName, ".pdf",
                    new File(System.getProperty("user.dir")));
            os = new FileOutputStream(outputFile);

            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(processedHtml);
            try {
                renderer.getFontResolver().addFont(System.getProperty("user.dir") + "/src/main/resources/fonts/calibri.ttf",
                        "cp1251", BaseFont.EMBEDDED);
            } catch (Exception ex) {
                log.warn("Cannot set font");
            }
            renderer.layout();
            renderer.createPDF(os, false);
            renderer.finishPDF();
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
}
