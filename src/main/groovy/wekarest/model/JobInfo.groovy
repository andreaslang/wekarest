package wekarest.model

class JobInfo {

    static final enum JobStatus { RUNNING, FAILURE, SUCCESS
    }

    Date started

    Date finished

    JobStatus status

}
