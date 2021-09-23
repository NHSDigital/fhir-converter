/*
 * Copyright (C) 2021 NHS Digital.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nhsd.fhir.transformationenginepoc.transformer.factories;

import java.math.BigDecimal;
import java.util.Date;
import org.hl7.fhir.dstu2.model.CodeableConcept;
import org.hl7.fhir.dstu2.model.Coding;
import org.hl7.fhir.dstu2.model.DateTimeType;
import org.hl7.fhir.dstu2.model.Medication;
import org.hl7.fhir.dstu2.model.Medication.MedicationPackageComponent;
import org.hl7.fhir.dstu2.model.Medication.MedicationPackageContentComponent;
import org.hl7.fhir.dstu2.model.Medication.MedicationProductBatchComponent;
import org.hl7.fhir.dstu2.model.Medication.MedicationProductComponent;
import org.hl7.fhir.dstu2.model.Medication.MedicationProductIngredientComponent;
import org.hl7.fhir.dstu2.model.MedicationStatement;
import org.hl7.fhir.dstu2.model.MedicationStatement.MedicationStatementDosageComponent;
import org.hl7.fhir.dstu2.model.Ratio;
import org.hl7.fhir.dstu2.model.Reference;
import org.hl7.fhir.dstu2.model.SimpleQuantity;
import org.hl7.fhir.dstu2.model.Timing;

/**
 *
 * @author tim.coates@nhs.net
 */
public class DSTU2ResourceFactory {

    public MedicationStatement GetMedicationStatement() {
        MedicationStatement statement = new MedicationStatement();
        statement.setId("my_test_id");
        Reference patref = new Reference().setReference("Patient/pat1");
        statement.setPatient(patref);
        statement.setInformationSource(patref);
        statement.setDateAsserted(new Date(2020, 5, 22));
        statement.setStatus(MedicationStatement.MedicationStatementStatus.COMPLETED);
        statement.setWasNotTaken(false);
        Coding coding = new Coding()
                .setSystem("http://snomed.info/sct")
                .setCode("22253000")
                .setDisplay("Pain");
        CodeableConcept reason = new CodeableConcept(coding);
        statement.setReasonForUse(reason);
        statement.setEffective(new DateTimeType("2015-01-23"));
        statement.setNote("Patient indicates they miss the occasional dose");
        Reference medref = new Reference("Medication/MedicationExample7");
        statement.setMedication(medref);
        MedicationStatementDosageComponent dosage = new MedicationStatementDosageComponent();
        dosage.setText("one tablet four times daily as needed for pain");
        Timing.TimingRepeatComponent repeat = new Timing.TimingRepeatComponent()
                .setFrequency(4)
                .setPeriod(BigDecimal.valueOf(1L))
                .setPeriodUnits(Timing.UnitsOfTime.D);
        Timing timing = new Timing()
                .setRepeat(repeat);
        dosage.setTiming(timing);
        //dosage.setAsNeeded(false);

        Coding routecoding = new Coding()
                .setSystem("http://snomed.info/sct")
                .setCode("260548002")
                .setDisplay("Oral");
        CodeableConcept route = new CodeableConcept(routecoding);
        dosage.setRoute(route);

        dosage.setQuantity(new SimpleQuantity().setValue(BigDecimal.valueOf(1L)));
        Ratio ratio = new Ratio();
        ratio.setNumerator(new SimpleQuantity()
                .setValue(BigDecimal.valueOf(4L))
                .setUnit("tablets")
                .setSystem("http://snomed.info/sct")
                .setCode("385055001")
        );
        ratio.setDenominator(new SimpleQuantity()
                .setValue(BigDecimal.valueOf(1L))
                .setSystem("http://unitsofmeasure.org")
                .setCode("d")
        );
        dosage.setMaxDosePerPeriod(ratio);

        statement.addDosage(dosage);
        return statement;
    }

    public Medication GetMedication() {
        Medication medication = new Medication();
        medication.setId("my_test_id");
        Coding coding = new Coding()
                .setSystem("http://www.fda.gov/Drugs/InformationOnDrugs/ucm142438.htm")
                .setCode("54569-2931")
                .setDisplay("Amoxicillin Powder, for Suspension 250mg/5ml");
        CodeableConcept code = new CodeableConcept()
                .addCoding(coding);
        medication.setCode(code);
        medication.setIsBrand(true);
        Reference manref = new Reference("http://www.a-smeds.com/fhirresource/1");        
        medication.setManufacturer(manref);
        
        MedicationProductComponent medprod = new MedicationProductComponent();
        Coding prodformcode = new Coding()
                .setSystem("http://snomed.info/sct")
                .setCode("42108003")
                .setDisplay("Powder for Suspension");
        CodeableConcept form = new CodeableConcept(prodformcode);
        medprod.setForm(form);
        
        MedicationProductIngredientComponent ingred = new MedicationProductIngredientComponent();
        Reference itemref = new Reference().setDisplay("Amoxicillin");
        ingred.setItem(itemref);
        
        Ratio ratio = new Ratio();
        ratio.setNumerator(new SimpleQuantity()
                .setValue(BigDecimal.valueOf(5))
                .setSystem("http://unitsofmeasure.org")
                .setCode("mg")
        );

        ratio.setDenominator(new SimpleQuantity()
                .setValue(BigDecimal.valueOf(250))
                .setSystem("http://unitsofmeasure.org")
                .setCode("mL")
        );
        ingred.setAmount(ratio);
        
        medprod.addIngredient(ingred);
        MedicationProductBatchComponent batch = new MedicationProductBatchComponent();
        batch.setLotNumber("12345");
        batch.setExpirationDate(new Date(2020,12,25));
        medprod.addBatch(batch);
        
        medication.setProduct(medprod);
        
        MedicationPackageComponent pack = new MedicationPackageComponent();
        Coding containercoding = new Coding()
                .setSystem("http://snomed.info/sct")
                .setCode("419672006")
                .setDisplay("Bottle - unit of product usage");
        CodeableConcept container = new CodeableConcept(containercoding);
        pack.setContainer(container);
        
        MedicationPackageContentComponent packcomponent = new MedicationPackageContentComponent();
        Reference packitemref = new Reference("MedicationExample14");
        packcomponent.setItem(packitemref);
        SimpleQuantity amount = new SimpleQuantity();
        amount.setValue(BigDecimal.valueOf(7.5))
                .setSystem("http://unitsofmeasure.org")
                .setUnit("gm");
        
        packcomponent.setAmount(amount);
        pack.addContent(packcomponent);
        
        medication.setPackage(pack);

        return medication;
    }
}
