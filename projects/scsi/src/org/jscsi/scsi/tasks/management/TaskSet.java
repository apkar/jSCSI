package org.jscsi.scsi.tasks.management;

import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;

import org.jscsi.scsi.tasks.Task;
import org.jscsi.scsi.transport.Nexus;

/**
 * A SAM-2 task set. The task set implements the BlockingQueue interface and provides additional
 * methods for removing a single task identified by a task tag or all tasks.
 * <p>
 * Some implementations may provide one task set for all I_T nexuses. In such cases task management
 * commands will affect outstanding commands for all initiators. 
 */
public interface TaskSet extends BlockingQueue<Task>
{
   
   /**
    * Aborts and removes the task specified by the given I_T_L_Q nexus. The caller obtains the
    * results of {@link Task#abort()}.
    * <p>
    * This method should be used when removing a task outside the normal execution flow. One
    * such usage is in processing the SAM-2 <code>ABORT TASK</code> task management function.
    * 
    * @param nexus An I_T_L_Q nexus identifying a given task.
    * @return The results of {@link Task#abort()}.
    * @throws NoSuchElementException If a task with the given task tag does not exist.
    * @throws InterruptedException If the thread is interrupted during the abort attempt.
    * @throws IllegalArgumentException If an I_T_L_Q nexus is not provided.
    */
   boolean remove(Nexus nexus)
      throws NoSuchElementException, InterruptedException, IllegalArgumentException;
   
   /**
    * Aborts all in-progress tasks and clears the task set for the given I_T_L nexus. Succeeds even
    * if some tasks fail to abort. 
    * <p>
    * Implementations which maintain a separate task set per I_T nexus will abort only those tasks
    * which match the indicated initiator. Implementations which maintain a shared task set will
    * abort all tasks for all initiators.
    * 
    * @param nexus An I_T_L nexus identifying a given task set.
    * @throws InterruptedException If the thread is interrupted during the abort attempt.
    * @throws IllegalArgumentException If an I_T_L nexus is not provided.
    */
   void clear(Nexus nexus) throws InterruptedException, IllegalArgumentException;
   
   
   /**
    * Aborts in-progress tasks and clears the task set of tasks that were created by the SCSI
    * initiator port and routed through the SCSI target port indicated by the given I_T_L nexus.
    * Succeeds even if some tasks fail to abort.
    * <p>
    * Some simple implementations may call {@link #clear(Nexus)} from this method. This will
    * result in less fine-grain task management and should not be used for logical units which
    * aim to properly support multiple simultaneous initiator connections.
    * 
    * @param nexus An I_T_L nexus identifying a source initiator and intermediate port.
    * @throws InterruptedException If the thread is interrupted during the abort attempt.
    * @throws IllegalArgumentException If an I_T_L nexus is not provided.
    */
   void abort(Nexus nexus) throws InterruptedException, IllegalArgumentException;
}
