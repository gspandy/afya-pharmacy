import org.joda.time.LocalDate
import org.joda.time.Period
import org.joda.time.PeriodType
import org.joda.time.Years

employeeId = parameters.partyId
List familyDependantList = delegator.findByAnd("EmployeeDependantPartyRelationshipTypeView",["employeeId":employeeId]);


children = [];
childrenUnder18 = [];
spouse = null;
for(Map familyDependent:familyDependantList){
if(familyDependent.partyRelationshipName.equalsIgnoreCase("wife")){
   spouse =  familyDependent;
}else if(familyDependent.partyRelationshipName.equalsIgnoreCase("husband")){
    spouse =  familyDependent;
}else if(familyDependent.partyRelationshipName.equalsIgnoreCase("son") || familyDependent.partyRelationshipName.equalsIgnoreCase("daughter")){
    children.add(familyDependent);
    int year = getAge(familyDependent.dateOfBirth);
    if(year<18){
        childrenUnder18.add(familyDependent)
    }
}
}



def getAge(Date dob){
    Calendar cal = Calendar.getInstance();
    cal.setTime(dob);
    int year = cal.get(Calendar.YEAR);
    int month = cal.get(Calendar.MONTH)+1;
    int day = cal.get(Calendar.DAY_OF_MONTH);
    LocalDate birthdate = new LocalDate (year, month, day);
    LocalDate now = new LocalDate();
    Years age = Years.yearsBetween(birthdate, now);
    Period period = new Period(birthdate, now, PeriodType.yearMonthDay());
    return period.getYears()
}


context.childrenUnder18 = childrenUnder18;
context.children = children;
context.spouse =spouse