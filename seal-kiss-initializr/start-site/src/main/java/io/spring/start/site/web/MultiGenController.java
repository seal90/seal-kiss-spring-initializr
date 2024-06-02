package io.spring.start.site.web;

import com.google.common.collect.Lists;
import io.spring.initializr.generator.buildsystem.BuildSystem;
import io.spring.initializr.generator.buildsystem.maven.MavenBuildSystem;
import io.spring.initializr.generator.io.template.TemplateRenderer;
import io.spring.initializr.generator.language.java.JavaLanguage;
import io.spring.initializr.generator.packaging.jar.JarPackaging;
import io.spring.initializr.generator.project.*;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.web.project.*;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.UnixStat;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * ProjectGenerationController
 */
@Controller
public class MultiGenController {

    private static final Log logger = LogFactory.getLog(MultiGenController.class);

    private static final List<String> ALLOW_RENDER_TYPE_FILE = Lists.newArrayList(".java", ".properties", ".xml", ".md", ".gitignore", ".yml", ".MD");

    private InitializrMetadataProvider metadataProvider;

    private ProjectGenerationInvoker<ProjectRequest> projectGenerationInvoker;

//    @Autowired
    private TemplateRenderer fileTemplateRenderer;

    private TemplateRenderer stringTemplateRenderer;

    private String sealParentVersion;

    public MultiGenController(
            InitializrMetadataProvider metadataProvider,
            ObjectProvider<ProjectRequestPlatformVersionTransformer> platformVersionTransformer,
            ApplicationContext applicationContext, String sealParentVersion) {
        this.metadataProvider = metadataProvider;
        ProjectGenerationInvoker<ProjectRequest> projectGenerationInvoker = new ProjectGenerationInvoker<>(
                applicationContext, new DefaultProjectRequestToDescriptionConverter(platformVersionTransformer
                .getIfAvailable(DefaultProjectRequestPlatformVersionTransformer::new)));
        this.projectGenerationInvoker = projectGenerationInvoker;
        this.fileTemplateRenderer = new SealFileMustacheTemplateRenderer("multi_templates");
        this.stringTemplateRenderer = new SealStringMustacheTemplateRenderer();
        this.sealParentVersion = sealParentVersion;
    }

    @RequestMapping(
            path = {"/multi_starter.zip"},
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    public ResponseEntity<byte[]> springZip(WebProjectRequest request) throws IOException, NoSuchMethodException {


        ProjectGenerationResult result1 = this.projectGenerationInvoker.invokeProjectStructureGeneration(request);

        MutableProjectDescription description = new MutableProjectDescription();
        description.setGroupId(request.getGroupId());
        description.setArtifactId(request.getArtifactId());
        description.setName(request.getName());
        description.setDescription(request.getDescription());
        description.setPackageName(request.getPackageName());

        description.setPackaging(new JarPackaging());
        description.setLanguage(new JavaLanguage("21"));

        description.setBuildSystem(new MavenBuildSystem());

        Map<String, String> model = new HashMap<>();
        model.put("MAVEN_GROUP_ID", request.getGroupId());
        model.put("MAVEN_ARTIFACT_ID", request.getArtifactId());
        model.put("MAVEN_NAME", request.getName());
        model.put("MAVEN_DESCRIPTION", request.getDescription());
        model.put("MAVEN_PACKAGE_NAME", request.getPackageName());

        model.put("PACKAGE_NAME_PATH", request.getPackageName().replaceAll("\\.", "/"));
        model.put("PACKAGE_NAME_URI_PATH", request.getPackageName().replaceAll("\\.", "%2F"));

        model.put("SEAL-KISS-PARENT-VERSION", sealParentVersion);
        Path rootDirectory = Files.createTempDirectory("project-");
        ProjectGenerationResult result = BeanUtils.instantiateClass(ProjectGenerationResult.class.getDeclaredConstructor(ProjectDescription.class, Path.class), description, rootDirectory);
//        ProjectGenerationResult(description, rootDirectory);

        ClassPathResource classPathResource = new ClassPathResource("/multi_templates");
        File file = classPathResource.getFile();
        String srcRootDirectory = file.getPath();
        genCode(file, srcRootDirectory, rootDirectory.toFile().getPath(), model);

        Path archive = this.createArchive(result, "zip", ZipArchiveOutputStream::new, ZipArchiveEntry::new, ZipArchiveEntry::setUnixMode);
        return this.upload(archive, result.getRootDirectory(), this.generateFileName(result.getProjectDescription().getArtifactId(), "zip"), "application/zip");
    }

    private void genCode(File file, String srcRootDirectory, String targetRootDirectory, Map<String, String> model) throws IOException {
        if(file.isDirectory()) {
            File[] files = file.listFiles();
            for(File inFile : files) {
                genCode(inFile, srcRootDirectory, targetRootDirectory, model);
            }
        } else {
            String fileDir = file.getParent();
            String fileRelativeDir = fileDir.substring(srcRootDirectory.length());
            String renderFileRelativeDir = stringTemplateRenderer.render(fileRelativeDir, model);
            String targetDir = targetRootDirectory + renderFileRelativeDir;
            File targetFileDir = new File(targetDir);
            if(!targetFileDir.exists()){
                targetFileDir.mkdirs();
            }
//            String fileContent = Files.readString(file.toPath());
//            String rendererContent = "";
//            if(StringUtils.hasText(fileContent)) {
//                rendererContent = mustacheTemplateRenderer.render(fileContent, model);
//            }
            String fileName = file.getName();
            String renderFileName = stringTemplateRenderer.render(fileName, model);
            Path targetFilePath = new File(targetDir+"/"+renderFileName).toPath();
            String fileSuffixName = "NO_RENDER";
            if(fileName.lastIndexOf(".") > 0) {
                fileSuffixName = fileName.substring(fileName.lastIndexOf("."));
            }

            if(ALLOW_RENDER_TYPE_FILE.contains(fileSuffixName)) {
                String rendererContent = fileTemplateRenderer.render(fileRelativeDir + "/" + fileName, model);
                Files.writeString(targetFilePath, rendererContent);
            } else {
                Files.copy(file.toPath(), targetFilePath);
            }
        }
    }


    private <T extends ArchiveEntry> Path createArchive(ProjectGenerationResult result, String fileExtension,
                                                        Function<OutputStream, ? extends ArchiveOutputStream> archiveOutputStream,
                                                        BiFunction<File, String, T> archiveEntry, BiConsumer<T, Integer> setMode) throws IOException {
        Path archive = this.projectGenerationInvoker.createDistributionFile(result.getRootDirectory(),
                "." + fileExtension);
        String wrapperScript = getWrapperScript(result.getProjectDescription());
        try (ArchiveOutputStream output = archiveOutputStream.apply(Files.newOutputStream(archive))) {
            Files.walk(result.getRootDirectory())
                    .filter((path) -> !result.getRootDirectory().equals(path))
                    .forEach((path) -> {
                        try {
                            String entryName = getEntryName(result.getRootDirectory(), path);
                            T entry = archiveEntry.apply(path.toFile(), entryName);
                            setMode.accept(entry, getUnixMode(wrapperScript, entryName, path));
                            output.putArchiveEntry(entry);
                            if (!Files.isDirectory(path)) {
                                Files.copy(path, output);
                            }
                            output.closeArchiveEntry();
                        }
                        catch (IOException ex) {
                            throw new IllegalStateException(ex);
                        }
                    });
        }
        return archive;
    }

    private String getEntryName(Path root, Path path) {
        String entryName = root.relativize(path).toString().replace('\\', '/');
        if (Files.isDirectory(path)) {
            entryName += "/";
        }
        return entryName;
    }

    private int getUnixMode(String wrapperScript, String entryName, Path path) {
        if (Files.isDirectory(path)) {
            return UnixStat.DIR_FLAG | UnixStat.DEFAULT_DIR_PERM;
        }
        return UnixStat.FILE_FLAG | (entryName.equals(wrapperScript) ? 0755 : UnixStat.DEFAULT_FILE_PERM);
    }

    private String generateFileName(String artifactId, String extension) {
        String candidate = (StringUtils.hasText(artifactId) ? artifactId
                : this.metadataProvider.get().getArtifactId().getContent());
        try {
            return URLEncoder.encode(candidate, "UTF-8") + "." + extension;
        }
        catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException("Cannot encode URL", ex);
        }
    }

    private static String getWrapperScript(ProjectDescription description) {
        BuildSystem buildSystem = description.getBuildSystem();
        String script = buildSystem.id().equals(MavenBuildSystem.ID) ? "mvnw" : "gradlew";
        return (description.getBaseDirectory() != null) ? description.getBaseDirectory() + "/" + script : script;
    }

    private ResponseEntity<byte[]> upload(Path archive, Path dir, String fileName, String contentType)
            throws IOException {
        byte[] bytes = Files.readAllBytes(archive);
        logger.info(String.format("Uploading: %s (%s bytes)", archive, bytes.length));
        ResponseEntity<byte[]> result = createResponseEntity(bytes, contentType, fileName);
        this.projectGenerationInvoker.cleanTempFiles(dir);
        return result;
    }

    private ResponseEntity<byte[]> createResponseEntity(byte[] content, String contentType, String fileName) {
        String contentDispositionValue = "attachment; filename=\"" + fileName + "\"";
        return ResponseEntity.ok()
                .header("Content-Type", contentType)
                .header("Content-Disposition", contentDispositionValue)
                .body(content);
    }
}
