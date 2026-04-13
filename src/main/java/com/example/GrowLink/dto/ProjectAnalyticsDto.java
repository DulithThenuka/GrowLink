package com.example.GrowLink.dto;

public class ProjectAnalyticsDto {

    private long totalTasks;
    private long todoTasks;
    private long inProgressTasks;
    private long doneTasks;
    private long totalMembers;
    private long overdueTasks;
    private long unassignedTasks;
    private long highPriorityTasks;
    private int completionPercentage;

    public long getTotalTasks() {
        return totalTasks;
    }

    public void setTotalTasks(long totalTasks) {
        this.totalTasks = totalTasks;
    }

    public long getTodoTasks() {
        return todoTasks;
    }

    public void setTodoTasks(long todoTasks) {
        this.todoTasks = todoTasks;
    }

    public long getInProgressTasks() {
        return inProgressTasks;
    }

    public void setInProgressTasks(long inProgressTasks) {
        this.inProgressTasks = inProgressTasks;
    }

    public long getDoneTasks() {
        return doneTasks;
    }

    public void setDoneTasks(long doneTasks) {
        this.doneTasks = doneTasks;
    }

    public long getTotalMembers() {
        return totalMembers;
    }

    public void setTotalMembers(long totalMembers) {
        this.totalMembers = totalMembers;
    }

    public long getOverdueTasks() {
        return overdueTasks;
    }

    public void setOverdueTasks(long overdueTasks) {
        this.overdueTasks = overdueTasks;
    }

    public long getUnassignedTasks() {
        return unassignedTasks;
    }

    public void setUnassignedTasks(long unassignedTasks) {
        this.unassignedTasks = unassignedTasks;
    }

    public long getHighPriorityTasks() {
        return highPriorityTasks;
    }

    public void setHighPriorityTasks(long highPriorityTasks) {
        this.highPriorityTasks = highPriorityTasks;
    }

    public int getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(int completionPercentage) {
        this.completionPercentage = completionPercentage;
    }
}