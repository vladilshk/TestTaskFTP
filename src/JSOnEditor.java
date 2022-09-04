public class JSOnEditor {
    public String createJson() {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("\tstudents: [\n");

        json.append("\t]\n");
        json.append("}\n");

        return json.toString();
    }

    public String addStudent(String json, String student) {
        StringBuilder newJson = new StringBuilder();
        int jsEnd = json.indexOf("]");
        int inputIndex = whereToAdd(json, student);
        for (int i = 0; i < inputIndex - 1; i++) {
            newJson.append(json.charAt(i));
        }

        newJson.append(newStudent(amountOfStudents(json) + 1, student));
        int continueInd;
        if (json.indexOf("\t\t{", inputIndex) > 0){
            continueInd = json.indexOf("\t\t{", inputIndex);
        }
        else {
            continueInd = json.indexOf("\t]", inputIndex);
        }

        for (int i = continueInd; i < json.length(); i++) {
            newJson.append(json.charAt(i));
        }

        return newJson.toString();
    }

    public int whereToAdd(String json, String student){
        int jsEnd = json.indexOf("]");
        StringBuilder nextName = new StringBuilder();
        //if there are no students in json
        if(!json.contains("\t\t{")){
            return json.indexOf("[") + 2;
        }
        int inputIndex = json.indexOf("\n\t\t{");
        while(inputIndex < jsEnd){
            int nextNameBegin =  json.indexOf("\"name\": ", inputIndex) + 9;
            //if there are no more names after inputIndex
            if(nextNameBegin < inputIndex){
                return inputIndex;
            } else{
                int nextNameEnd = json.indexOf("\"", nextNameBegin);
                int idx = 0;
                for (int i = nextNameBegin; i < nextNameEnd; i++) {
                    nextName.append(json.charAt(i));
                }
                if(student.compareTo(String.valueOf(nextName)) <= 0 ) {
                    return inputIndex;
                }
                else{
                    //if there are some names after input index
                    if(json.indexOf("\t{", inputIndex) > 0) {
                        inputIndex = json.indexOf("\t}", inputIndex) + 2;
                    } else {
                        return inputIndex;
                    }
                }
            }
        }
        return inputIndex;
    }

    public String newStudent(int id, String name) {
        StringBuilder student = new StringBuilder();
        student.append("\n\t\t{\n");
        student.append("\t\t\t\"id\": " + id + "\n");
        student.append("\t\t\t\"name\": \"" + name + "\"\n");
        student.append("\t\t}\n");
        return student.toString();
    }

    public String deleteStudent(String json, int id) {
        int studentBegin = json.indexOf("\"id\": " + id) - 7;
        int studentEnd = json.indexOf("}", studentBegin + 7);
        StringBuilder newJson = new StringBuilder();
        for (int i = 0; i < studentBegin; i++) {
            newJson.append(json.charAt(i));
        }
        for (int i = studentEnd + 2; i < json.length(); i++) {
            newJson.append(json.charAt(i));
        }
        return newJson.toString();
    }

    public void sortStudents(String json){

    }

    public int amountOfStudents(String json){
        char c = '{';
        return (int) json.chars().filter(x->x==c).count() - 1;
    }

    /*public static void compareStrings(String student1, String student2 {

        int comparedResult = student1.compareTo(student2);

        if (comparedResult > 0) {
            System.out.println(student1 + " comes after " + student2);
        } else if (comparedResult < 0) {
            System.out.println(student1 + " comes before " + student2);
        } else {
            System.out.println(student1 + " is equal to " + student2);
        }


    }*/


    public void smth() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\t\t{\n");
        stringBuilder.append("\t\t\t\"id\": 1\n");
        stringBuilder.append("\t\t\t\"name\": \"Student1\"\n");
        stringBuilder.append("\t\t},\n");
        stringBuilder.append("\t\t{\n");
        stringBuilder.append("\t\t\t\"id\": 2\n");
        stringBuilder.append("\t\t\t\"name\": \"Student2\"\n");
        stringBuilder.append("\t\t},\n");
        stringBuilder.append("\t\t{\n");
        stringBuilder.append("\t\t\t\"id\": 3\n");
        stringBuilder.append("\t\t\t\"name\": \"Student3\"\n");
        stringBuilder.append("\t\t}\n");
    }
}
