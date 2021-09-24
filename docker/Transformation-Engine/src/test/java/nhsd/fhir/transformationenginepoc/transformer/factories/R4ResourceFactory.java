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

import ca.uhn.fhir.model.api.ExtensionDt;
import ca.uhn.fhir.model.primitive.BooleanDt;
import java.math.BigDecimal;
import java.util.Date;
import org.hl7.fhir.r4.model.Annotation;
import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Dosage;
import org.hl7.fhir.r4.model.Dosage.DosageDoseAndRateComponent;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Medication;
import org.hl7.fhir.r4.model.Medication.MedicationBatchComponent;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.MedicationStatement;
import org.hl7.fhir.r4.model.Ratio;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.SimpleQuantity;
import org.hl7.fhir.r4.model.Timing;
/**
 *
 * @author tim.coates@nhs.net
 */
public class R4ResourceFactory {
    
    public MedicationRequest GetMedicationRequest() {
        MedicationRequest request = new MedicationRequest();
        request.setId("my_test_id");
        Identifier id = new Identifier();
        id.setUse(Identifier.IdentifierUse.OFFICIAL);
        id.setSystem("http://www.bmc.nl/portal/prescriptions");
        id.setValue("12345");
        request.addIdentifier(id);
        request.setStatus(MedicationRequest.MedicationRequestStatus.ACTIVE);
        request.setIntent(MedicationRequest.MedicationRequestIntent.ORDER);
        Reference med1 = new Reference().setReference("\"Medication/med0316").setDisplay("prescribed medication");
        request.setMedication(med1);
        Reference subj = new Reference().setReference("Patient/pat1").setDisplay("Donald Duck");
        request.setSubject(subj);
        Reference cont = new Reference().setReference("Encounter/f001").setDisplay("encounter that leads to this prescription");
        request.setEncounter(cont);
        request.setAuthoredOn(new Date(2021, 5, 22));
        Reference agent = new Reference().setReference("Practitioner/f007").setDisplay("Patrick Pump");
        Reference org = new Reference().setReference("Organization/f002");
        //MedicationRequest.MedicationRequestRequesterComponent reqr = new MedicationRequest.MedicationRequestRequesterComponent().setAgent(agent).setOnBehalfOf(org);
        request.setRequester(agent);
        
        Coding theCoding = new Coding().setSystem("http://snomed.info/sct").setCode("59621000").setDisplay("Essential hypertension (disorder)");
        CodeableConcept reasoncode = new CodeableConcept().addCoding(theCoding);
        request.addReasonCode(reasoncode);
        
        Dosage dosage = new Dosage().setSequence(1).setText("Take one tablet daily as directed");
        request.addDosageInstruction(dosage);
        return request;
    }
    
    public MedicationStatement GetMedicationStatement() {
        MedicationStatement statement = new MedicationStatement();
            statement.setId("my_test_id");
            Reference patref = new Reference().setReference("Patient/pat1");
            statement.setSubject(patref);
            statement.setInformationSource(patref);
            statement.setDateAsserted(new Date(2020, 5, 22));
            statement.setStatus(MedicationStatement.MedicationStatementStatus.COMPLETED);

            Coding coding = new Coding().setSystem("http://snomed.info/sct").setCode("22253000").setDisplay("Pain");
            CodeableConcept reason = new CodeableConcept(coding);
            statement.addReasonCode(reason);
            statement.setEffective(new DateTimeType("2015-01-23"));
            statement.addNote(new Annotation().setText("Patient indicates they miss the occasional dose"));
            Reference medref = new Reference("Medication/MedicationExample7");
            statement.setMedication(medref);

            Dosage dosage = new Dosage();
            dosage.setText("one tablet four times daily as needed for pain");
            Timing.TimingRepeatComponent repeat = new Timing.TimingRepeatComponent().setFrequency(4).setPeriod(BigDecimal.valueOf(1L)).setPeriodUnit(Timing.UnitsOfTime.D);
            Timing timing = new Timing();
            timing.setRepeat(repeat);
            dosage.setTiming(timing);
            //dosage.setAsNeeded(false);
            Coding routecoding = new Coding().setSystem("http://snomed.info/sct").setCode("260548002").setDisplay("Oral");
            CodeableConcept route = new CodeableConcept(routecoding);
            dosage.setRoute(route);
            
            DosageDoseAndRateComponent doserate = new DosageDoseAndRateComponent();
            doserate.setDose(new SimpleQuantity().setValue(BigDecimal.valueOf(1L)));
            dosage.addDoseAndRate(doserate);
            Ratio ratio = new Ratio();
            ratio.setNumerator(new SimpleQuantity().setValue(BigDecimal.valueOf(4L)).setUnit("tablets").setSystem("http://snomed.info/sct").setCode("385055001"));
            ratio.setDenominator(new SimpleQuantity().setValue(BigDecimal.valueOf(1L)).setSystem("http://unitsofmeasure.org").setCode("d"));
            dosage.setMaxDosePerPeriod(ratio);
            statement.addDosage(dosage);
        return statement;
    }
    
    public Medication GetMedication() {
        Medication medication = new Medication();
        medication.getMeta().addProfile("https://fhir.hl7.org.uk/StructureDefinition/UKCore-Medication");
        
        medication.setId("my_test_id");
        Coding coding = new Coding()
                .setSystem("http://www.fda.gov/Drugs/InformationOnDrugs/ucm142438.htm")
                .setCode("54569-2931")
                .setDisplay("Amoxicillin Powder, for Suspension 250mg/5ml");
        CodeableConcept code = new CodeableConcept()
                .addCoding(coding);
        medication.setCode(code);

        // We need to add extension hhttp://hl7.org/fhir/3.0/StructureDefinition/extension-Medication.isBrand 
        Extension ext = new Extension();
        ext.setUrl("http://hl7.org/fhir/3.0/StructureDefinition/extension-Medication.isBrand");
        ext.setValue(new BooleanType(Boolean.TRUE));

        medication.addExtension(ext);
        
        medication.setBatch(new MedicationBatchComponent().setLotNumber("12345").setExpirationDate(new Date(2020,12,25)));
        
        Reference manref = new Reference("http://www.a-smeds.com/fhirresource/1");        
        medication.setManufacturer(manref);
        /**
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
**/
        return medication;
    }
}
