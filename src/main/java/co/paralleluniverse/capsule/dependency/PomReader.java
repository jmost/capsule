package co.paralleluniverse.capsule.dependency;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.Model;
import org.apache.maven.model.Repository;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 *
 * @author pron
 */
public class PomReader {
    private final Model pom;

    public PomReader(JarFile jar, String pomFile) {
        try {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            this.pom = reader.read(jar.getInputStream(jar.getEntry(pomFile)));
        } catch (IOException | XmlPullParserException e) {
            throw new RuntimeException("Error trying to read pom.", e);
        }
    }

    public String getAppId() {
        pom.getArtifactId();
        pom.getGroupId();
        pom.getVersion();
        pom.getId();
    }

    public List<String> getRepositories() {
        final List<Repository> repos = pom.getRepositories();
        if (repos == null)
            return null;
        final List<String> repositories = new ArrayList<>();
        
        return repositories;
    }

    public List<String> getDependencies() throws IOException {
        List<Dependency> deps = pom.getDependencies();
        if (deps == null)
            return null;

        final List<String> dependencies = new ArrayList<>();
        for (Dependency dep : deps) {
            if (!dep.isOptional()) {
                String coords = dep.getGroupId() + ":" + dep.getArtifactId() + ":" + dep.getVersion()
                        + (dep.getClassifier() != null && !dep.getClassifier().isEmpty() ? ":" + dep.getClassifier() : "");
                List<Exclusion> exclusions = dep.getExclusions();
                if (exclusions != null && !exclusions.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append('(');
                    for (Exclusion ex : exclusions)
                        sb.append(ex.getGroupId()).append(':').append(ex.getArtifactId()).append(',');
                    sb.delete(sb.length() - 1, sb.length());
                    sb.append(')');
                    coords += sb.toString();
                }
                dependencies.add(coords);
            }
        }
        return dependencies;
    }
}