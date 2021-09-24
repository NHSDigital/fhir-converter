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
import org.hl7.fhir.dstu3.model.Annotation;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.hl7.fhir.dstu3.model.Dosage;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Medication;
import org.hl7.fhir.dstu3.model.Medication.MedicationIngredientComponent;
import org.hl7.fhir.dstu3.model.Medication.MedicationPackageBatchComponent;
import org.hl7.fhir.dstu3.model.Medication.MedicationPackageComponent;
import org.hl7.fhir.dstu3.model.Medication.MedicationPackageContentComponent;
import org.hl7.fhir.dstu3.model.MedicationRequest;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.MedicationStatement;
import org.hl7.fhir.dstu3.model.MedicationStatement.MedicationStatementTaken;
import org.hl7.fhir.dstu3.model.Ratio;
import org.hl7.fhir.dstu3.model.SimpleQuantity;
import org.hl7.fhir.dstu3.model.Timing;
import org.hl7.fhir.dstu3.model.Timing.TimingRepeatComponent;

/**
 *
 * @author tim.coates@nhs.net
 */
public class STU3ResourceFactory {
        
    public MedicationRequest GetMedicationRequest() {
        MedicationRequest request = new MedicationRequest();
        request.setId("my_test_id");
        Identifier newID = new Identifier();
        newID.setUse(Identifier.IdentifierUse.OFFICIAL);
        newID.setSystem("http://www.bmc.nl/portal/prescriptions");
        newID.setValue("12345");
        request.addIdentifier(newID);
        request.setStatus(MedicationRequest.MedicationRequestStatus.ACTIVE);
        request.setIntent(MedicationRequest.MedicationRequestIntent.ORDER);
        Reference med1 = new Reference().setReference("\"Medication/med0316").setDisplay("prescribed medication");
        request.setMedication(med1);
        Reference subj = new Reference().setReference("Patient/pat1").setDisplay("Donald Duck");
        request.setSubject(subj);
        Reference cont = new Reference().setReference("Encounter/f001").setDisplay("encounter that leads to this prescription");
        request.setContext(cont);
        request.setAuthoredOn(new Date(2021, 5, 22));
        Reference agent = new Reference().setReference("Practitioner/f007").setDisplay("Patrick Pump");
        Reference org = new Reference().setReference("Organization/f002");
        MedicationRequest.MedicationRequestRequesterComponent reqr = new MedicationRequest.MedicationRequestRequesterComponent().setAgent(agent);
        //reqr.setOnBehalfOf(org);
        request.setRequester(reqr);
        Coding theCoding = new Coding().setSystem("http://snomed.info/sct").setCode("59621000").setDisplay("Essential hypertension (disorder)");
        CodeableConcept reasoncode = new CodeableConcept().addCoding(theCoding);
        request.addReasonCode(reasoncode);
        Dosage dosage = new Dosage().setSequence(1).setText("Take one tablet daily as directed");
        request.addDosageInstruction(dosage);
        return request;
    }

    public MedicationStatement GetMedicationStatement(){
        MedicationStatement statement = new MedicationStatement();
        statement.setId("my_test_id");
        Reference patref = new Reference().setReference("Patient/pat1");
        statement.setSubject(patref);
        statement.setInformationSource(patref);
        statement.setDateAsserted(new Date(2020, 5, 22));
        statement.setStatus(MedicationStatement.MedicationStatementStatus.COMPLETED);
        statement.setTaken(MedicationStatementTaken.Y);
        Coding coding = new Coding().setSystem("http://snomed.info/sct").setCode("22253000").setDisplay("Pain");
        CodeableConcept reason = new CodeableConcept(coding);
        statement.addReasonCode(reason);
        statement.setEffective(new DateTimeType("2015-01-23"));
        statement.addNote(new Annotation().setText("Patient indicates they miss the occasional dose"));
        Reference medref = new Reference("Medication/MedicationExample7");
        statement.setMedication(medref);

        Dosage dosage = new Dosage();
        dosage.setText("one tablet four times daily as needed for pain");
        TimingRepeatComponent repeat = new TimingRepeatComponent().setFrequency(4).setPeriod(BigDecimal.valueOf(1L)).setPeriodUnit(Timing.UnitsOfTime.D);
        Timing timing = new Timing();
        timing.setRepeat(repeat);
        dosage.setTiming(timing);
        //dosage.setAsNeeded(false);
        Coding routecoding = new Coding().setSystem("http://snomed.info/sct").setCode("260548002").setDisplay("Oral");
        CodeableConcept route = new CodeableConcept(routecoding);
        dosage.setRoute(route);

        dosage.setDose(new SimpleQuantity().setValue(BigDecimal.valueOf(1L)));
        Ratio ratio = new Ratio();
        ratio.setNumerator(new SimpleQuantity().setValue(BigDecimal.valueOf(4L)).setUnit("tablets").setSystem("http://snomed.info/sct").setCode("385055001"));
        ratio.setDenominator(new SimpleQuantity().setValue(BigDecimal.valueOf(1L)).setSystem("http://unitsofmeasure.org").setCode("d"));
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
        
        Coding prodformcode = new Coding()
                .setSystem("http://snomed.info/sct")
                .setCode("42108003")
                .setDisplay("Powder for Suspension");
        CodeableConcept form = new CodeableConcept(prodformcode);
        medication.setForm(form);
        
        MedicationIngredientComponent ingred = new MedicationIngredientComponent();
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
        
        medication.addIngredient(ingred);
        
        MedicationPackageComponent medpac = new MedicationPackageComponent();

        MedicationPackageBatchComponent batch = new MedicationPackageBatchComponent();
        batch.setLotNumber("12345");
        batch.setExpirationDate(new Date(2020,12,25));
        medpac.addBatch(batch);        

        Coding containercoding = new Coding()
                .setSystem("http://snomed.info/sct")
                .setCode("419672006")
                .setDisplay("Bottle - unit of product usage");
        CodeableConcept container = new CodeableConcept(containercoding);
        medpac.setContainer(container);
        
        MedicationPackageContentComponent packcomponent = new MedicationPackageContentComponent();
        Reference packitemref = new Reference("MedicationExample14");
        packcomponent.setItem(packitemref);
        SimpleQuantity amount = new SimpleQuantity();
        amount.setValue(BigDecimal.valueOf(7.5))
                .setSystem("http://unitsofmeasure.org")
                .setUnit("gm");
        
        packcomponent.setAmount(amount);
        medpac.addContent(packcomponent);

        medication.setPackage(medpac);        
       
        return medication;
    }
}
