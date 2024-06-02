/*
 * Copyright 2012-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.start.site.support;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.generator.version.VersionParser;
import io.spring.initializr.metadata.DefaultMetadataElement;
import io.spring.initializr.web.support.InitializrMetadataUpdateStrategy;
import io.spring.initializr.web.support.SpringIoInitializrMetadataUpdateStrategy;

import org.apache.commons.compress.utils.IOUtils;
import org.springframework.web.client.RestTemplate;

/**
 * An {@link InitializrMetadataUpdateStrategy} that performs additional filtering of
 * versions available on spring.io.
 *
 * @author Stephane Nicoll
 */
public class StartInitializrMetadataUpdateStrategy extends SpringIoInitializrMetadataUpdateStrategy {

	private static final Comparator<DefaultMetadataElement> VERSION_METADATA_ELEMENT_COMPARATOR = new VersionMetadataElementComparator();

	private final ObjectMapper objectMapper;

	public StartInitializrMetadataUpdateStrategy(RestTemplate restTemplate, ObjectMapper objectMapper) {
		super(restTemplate, objectMapper);
		this.objectMapper = objectMapper;
	}

	@Override
	protected List<DefaultMetadataElement> fetchSpringBootVersions(String url) {
		InputStream metadataStream = this.getClass().getClassLoader().getResourceAsStream("project_metadata.json");
		JsonNode content = null;
		try {
			content = this.objectMapper.readTree(new String(IOUtils.toByteArray(metadataStream)));
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		List<DefaultMetadataElement> versions = getBootVersions(content);

		// List<DefaultMetadataElement> versions = super.fetchSpringBootVersions(url);
		return (versions != null) ? versions.stream().filter(this::isCompatibleVersion).collect(Collectors.toList())
				: null;
	}

	List<DefaultMetadataElement> getBootVersions(JsonNode content) {
		ArrayNode releases = (ArrayNode) content.get("_embedded").get("releases");
		List<DefaultMetadataElement> list = new ArrayList();
		Iterator var3 = releases.iterator();

		while (var3.hasNext()) {
			JsonNode node = (JsonNode) var3.next();
			DefaultMetadataElement versionMetadata = parseVersionMetadata(node);
			if (versionMetadata != null) {
				list.add(versionMetadata);
			}
		}

		list.sort(VERSION_METADATA_ELEMENT_COMPARATOR.reversed());
		return list;
	}

	private DefaultMetadataElement parseVersionMetadata(JsonNode node) {
		String versionId = node.get("version").textValue();
		Version version = VersionParser.DEFAULT.safeParse(versionId);
		if (version == null) {
			return null;
		}
		else {
			DefaultMetadataElement versionMetadata = new DefaultMetadataElement();
			versionMetadata.setId(versionId);
			versionMetadata.setName(determineDisplayName(version));
			versionMetadata.setDefault(node.get("current").booleanValue());
			return versionMetadata;
		}
	}

	private String determineDisplayName(Version version) {
		StringBuilder sb = new StringBuilder();
		sb.append(version.getMajor()).append(".").append(version.getMinor()).append(".").append(version.getPatch());
		if (version.getQualifier() != null) {
			sb.append(this.determineSuffix(version.getQualifier()));
		}

		return sb.toString();
	}

	private String determineSuffix(Version.Qualifier qualifier) {
		String id = qualifier.getId();
		if (id.equals("RELEASE")) {
			return "";
		}
		else {
			StringBuilder sb = new StringBuilder(" (");
			if (id.contains("SNAPSHOT")) {
				sb.append("SNAPSHOT");
			}
			else {
				sb.append(id);
				if (qualifier.getVersion() != null) {
					sb.append(qualifier.getVersion());
				}
			}

			sb.append(")");
			return sb.toString();
		}
	}

	private boolean isCompatibleVersion(DefaultMetadataElement versionMetadata) {
		Version version = Version.parse(versionMetadata.getId());
		return (version.getMajor() >= 3 && version.getMinor() >= 1);
	}

	private static final class VersionMetadataElementComparator implements Comparator<DefaultMetadataElement> {

		private static final VersionParser versionParser;

		private VersionMetadataElementComparator() {
		}

		@Override
		public int compare(DefaultMetadataElement o1, DefaultMetadataElement o2) {
			Version o1Version = versionParser.parse(o1.getId());
			Version o2Version = versionParser.parse(o2.getId());
			return o1Version.compareTo(o2Version);
		}

		static {
			versionParser = VersionParser.DEFAULT;
		}

	}

}
