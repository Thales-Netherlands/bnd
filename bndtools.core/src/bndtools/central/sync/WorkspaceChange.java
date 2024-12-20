package bndtools.central.sync;

import java.util.Collection;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import aQute.bnd.build.Workspace;
import aQute.bnd.service.RepositoryPlugin;

@Component
public class WorkspaceChange {
	final static IWorkspace	workspace	= ResourcesPlugin.getWorkspace();

	@Reference
	Workspace				ws;

	boolean					first		= true;
	volatile boolean		cancel		= false;

	@Activate
	void activate() {
		ws.on("workspace-changes")
			.initial(this::init)
			.repositoriesReady(this::done);
	}

	private void done(Collection<RepositoryPlugin> repos) {
		if (first) {
			first = false;
			return;
		}
		cancel = false;
		Job job = Job.create("workspace-changes", this::done0);
		job.schedule(2000);
	}

	private IStatus done0(IProgressMonitor monitor) {
		try {
			if (cancel || monitor.isCanceled())
				return Status.CANCEL_STATUS;

			if (!ResourcesPlugin.getWorkspace()
				.isAutoBuilding()) {
				return Status.warning(
					"[cnf change] Skip FULL build, because 'Build Automatically' is disabled.");
			}

			workspace.build(IncrementalProjectBuilder.FULL_BUILD, monitor);
		} catch (CoreException e) {
			return Status.error("trying to build", e);
		}
		return Status.OK_STATUS;
	}

	private void init(Workspace workspace) {
		cancel = true;
	}

}
