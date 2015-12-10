/** **************************************************************************************************************************/
/*  Created by alexsedova 2015/11/08
/*
/*  JobHelpers class contains triggers/configurations and so on call repeatedly by jobs in alljobs.dsl
/***************************************************************************************************************************/

public class JobHelpers {
    /*
    * Use this function to add log rotation to description section in job configuration
    *
    * @param context     Job instance
    * @param debug       debug mode
    */

    static void addDescriptionParam(context, debug = false) {
        if (debug == true) {
        } else {
            context.logRotator {
                numToKeep(20)
            }
        }
    }
    /*
   * Use this function to add GitLab trigger to the job configuration
   *
   * @param context     Job instance
   * @param debug       debug mode
   */

    static void addGitLabTrigger(context, debug = false) {
        if (debug == true) {
            println 'Debug is set to true - do not configure GitLab trigger'
        } else {
            context.configure { project ->
                project / triggers << 'com.dabsquared.gitlabjenkins.GitLabPushTrigger' {
                    spec ''
                    triggerOnPush true
                    triggerOnMergeRequest true
                    triggerOpenMergeRequestOnPush true
                    ciSkip true
                    setBuildDescription true
                    addNoteOnMergeRequest true
                    addVoteOnMergeRequest true
                    allowAllBranches false
                    includeBranchesSpec ''
                    excludeBranchesSpec ''
                }
            }
        }
    }
    /*
   * Use this function to configure Slack block to the job configuration
   *
   * @param context    Job instance
   * @param domain     Slack domain name
   * @param channel    Slack channel
   * @params failure,
   *         success,
   *         unst,
   *         back      Set notification
   * @param debug      debug mode
   */

    static void addSlackNotification(context, domain, channel, failure = true,
                                     success = true, unst = true, back = true, debug = false) {
        if (debug == true) {
        } else {
            context.publishers {
                slackNotifications {
                    teamDomain domain
                    projectChannel channel
                    notifyFailure failure
                    notifySuccess success
                    notifyUnstable unst
                    notifyBackToNormal back
                }
            }
        }
    }

    /*
   * Use this function to configure SCM block to job configuration
   *
   * @param context          Job instance
   * @param projecturl       URL to Gitlab project
   * @param credential       Credentials for gitlab access
   * @param branchnames      Specify branches
   * @param browsertype      Specify browser type
   * @param browserversion   and version
   * @param isRecursive      add recursive
   * @param debug            debug mode
   */
    // Branchnames array because of it can be many branches for job
    public static void addScmBlock(context, projecturl, credential,
                                   def branchnames = [] as String[], browsertype = '', browserversion = '',
                                   isRecursive = false, debug = false) {
        if (debug == true) {
        } else {
            context.scm {
                git {
                    remote {
                        url(projecturl)
                        credentials(credential)
                    }
                    for (branchname in branchnames) {
                        branch(branchname)
                    }
                    browser {
                        gitLab(browsertype, browserversion)
                    }
                    recursiveSubmodules isRecursive
                }
            }
        }
    }

    /*
    * Use this function to add log rotation to description section in job configuration
    *
    * @param context     Job instance
    * @param debug       debug mode
    */

    public static void addPretestedIntegration(context, debug = false) {
        if (debug == true) {
        } else {
            context.configure { project ->
                project / publishers << 'org.jenkinsci.plugins.pretestedintegration.PretestedIntegrationPostCheckout' {}
                project / buildWrappers << 'org.jenkinsci.plugins.pretestedintegration.PretestedIntegrationBuildWrapper' {
                    scmBridge('class': 'org.jenkinsci.plugins.pretestedintegration.scm.git.GitBridge') {
                        branch 'master'
                        integrationStrategy('class': 'org.jenkinsci.plugins.pretestedintegration.scm.git.SquashCommitStrategy')
                        repoName 'origin'
                    }
                    rollbackEnabled false
                }
            }
        }
    }
}