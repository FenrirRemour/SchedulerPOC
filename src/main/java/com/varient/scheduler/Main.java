package com.varient.scheduler;

import com.varient.job.SampleJob;
import org.apache.commons.cli.*;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

public class Main {
    public static void main(String[] args) {


        CommandLine cmd = defineCliOptions(args);

        String sehedularName = null;
        String optionString = null;
        String[] valueString = null;
        String startString = null;

        if (cmd.hasOption("s")) {

            sehedularName = cmd.getOptionValue("schedular");

            if (cmd.hasOption("o")) {

                optionString = cmd.getOptionValue("option");
                valueString = cmd.getOptionValues("value");
                startString = cmd.getOptionValue("start");

                try {

                    StdSchedulerFactory factory = new StdSchedulerFactory();
                    factory.initialize("quartz.properties");

                    //Scheduler scheduler = factory.getScheduler(sehedularName);
                    Scheduler scheduler = factory.getScheduler();

                    if ("create".equalsIgnoreCase(optionString)) {


                        // Check if scheduler is null (meaning it wasn't found)
                        if (scheduler == null) {
                            System.out.println("Scheduler with name " + sehedularName + " was not found.");
                        } else {

                            JobDetail job = JobBuilder.newJob(SampleJob.class)
                                    .withIdentity(valueString[0], "group1")
                                    .build();

                            // Trigger the job to run now, and then repeat every 10 seconds
                            Trigger trigger = TriggerBuilder.newTrigger()
                                    .withIdentity(valueString[0] + "Trigger", "group1")
                                    .startNow()
                                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                                            .withIntervalInSeconds(5)
                                            .repeatForever())
                                    .build();

                            scheduler.scheduleJob(job, trigger);

                            if("true".equalsIgnoreCase(startString)) {
                                scheduler.start();
                            }

                        }


                    } else if ("update".equalsIgnoreCase(optionString)) {

                        //if ("pause".equalsIgnoreCase(valueString[0]) || "resume".equalsIgnoreCase(valueString[0]) ) {
                            ////write a code to pause
                            for (String groupName : scheduler.getJobGroupNames()) {
                                for (JobKey jobKeyInGroup : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                                    if (jobKeyInGroup.getName().equals(valueString[1])) {

                                        if("pause".equalsIgnoreCase(valueString[0])) {
                                            scheduler.pauseJob(jobKeyInGroup);
                                        }else if("resume".equalsIgnoreCase(valueString[0])){
                                            scheduler.resumeJob(jobKeyInGroup);
                                        }
                                    }
                                }
                            }

                        //}

                    }

                } catch (SchedulerException e) {
                    throw new RuntimeException(e);
                }


            }


        }


        //logic();

    }


    private static CommandLine defineCliOptions(String[] args) {

        Options options = new Options();


        Option schedularOpt = Option.builder("s").longOpt("schedular")
                .argName("schedular")
                .hasArg()
                .required(true)
                .desc("Schedular name needed").build();
        options.addOption(schedularOpt);


        Option functionOpt = Option.builder("o").longOpt("option")
                .argName("option")
                .hasArg()
                .required(true)    //.optionalArg(false)
                .desc("Set option").build();
        //convertOpt.setRequired(false); // option 'C' is not mandatory
        options.addOption(functionOpt);


        Option startSchedularOpt = Option.builder("s").longOpt("start")
                .argName("start")
                .hasArg()
                .required(true)    //.optionalArg(false)
                .desc("Set option").build();
        startSchedularOpt.setRequired(false);
        options.addOption(startSchedularOpt);


        Option valueOpt = Option.builder("v").longOpt("value")
                .argName("value")
                .hasArg()
                .required(true)    //.optionalArg(false)
                .desc("Set value").build();
        //convertOpt.setRequired(false); // option 'C' is not mandatory
        valueOpt.setArgs(Option.UNLIMITED_VALUES);
        options.addOption(valueOpt);


        CommandLineParser parser = new GnuParser();
        HelpFormatter helper = new HelpFormatter();

        //CommandLine cmd=
        CommandLine cl = null;
        try {
            cl = parser.parse(options, args);
        } catch (ParseException e) {
            //Mandatory cli options are missing: " + e.getMessage());
            System.out.println(e.getMessage());
        }
        return cl;
    }


    public static void logic() {

        try {
            // Create a Scheduler
            //SchedulerFactory schedulerFactory = new StdSchedulerFactory();
            //schedulerFactory.initialize("quartz.properties");

            StdSchedulerFactory schedulerFactory = new StdSchedulerFactory();
            schedulerFactory.initialize("quartz.properties");

            //Scheduler scheduler = schedulerFactory.getScheduler();
            Scheduler scheduler = schedulerFactory.getScheduler("MySQLScheduler");

            // Define a job and tie it to our HelloWorldJob class
            JobDetail job = JobBuilder.newJob(SampleJob.class)
                    .withIdentity("myJob", "group1")
                    .build();

            // Trigger the job to run now, and then repeat every 10 seconds
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("myTrigger", "group1")
                    .startNow()
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            .withIntervalInSeconds(5)
                            .repeatForever())
                    .build();

            // Tell quartz to schedule the job using our trigger
            scheduler.scheduleJob(job, trigger);

            // Start the scheduler
            scheduler.start();

        } catch (SchedulerException se) {
            se.printStackTrace();
        }

    }


}