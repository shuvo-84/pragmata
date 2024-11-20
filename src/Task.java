import java.time.LocalDate;

public class Task {
    String description;
    LocalDate dueDate;
    public boolean completed;
    public Task(String description, LocalDate dueDate, boolean ok){
        this.description = description;
        this.dueDate = dueDate;
        if(ok)this.completed = true;
        else this.completed = false;
    }
    public String getDescription(){
        return this.description;
    }
    public LocalDate getdueDate(){
        return this.dueDate;
    }
    
    @Override
    public String toString(){
        return description+"\n"+"(Due: "+this.dueDate.toString()+")";
    }
    public String toString2(){
        return description+"\n"+"(Due: "+this.dueDate.toString()+")" + "\nCompleted✓✓✓✓✓";
    }
}
