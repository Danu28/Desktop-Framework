<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" indent="yes" />
	<xsl:template match="/">
		<xsl:element name="CaptureInfo">
			<!-- <xsl:element name="SentenceStyle"> -->
			<!-- <xsl:value-of select="//Header/Sentence/@Structure" /> -->
			<!-- </xsl:element> -->
			<!-- <xsl:element name="Formattype"> -->
			<!-- <xsl:value-of select="//Header/Sentence/@Format" /> -->
			<!-- </xsl:element> -->

			<xsl:for-each select="//Step">
				<xsl:choose>
					<xsl:when test="@IsStrayStep !='1' ">
						<xsl:call-template name="stepDetail">
						</xsl:call-template>
					</xsl:when>
				</xsl:choose>
			</xsl:for-each>
		</xsl:element>
	</xsl:template>

	<xsl:template name="stepDetail">
		<xsl:choose>
			<xsl:when
				test="Sentence/@Plain !='' and Sentence/@Plain != 'ExcludeEvent this item .'">
				<xsl:element name="Step">

					<xsl:attribute name="StepType">
                        <xsl:value-of
						select="@IsStrayStep" />
                  </xsl:attribute>

					<xsl:element name="StepData">

						<xsl:attribute name="StepSentence">
                        <xsl:value-of
							select="Sentence/@Plain" />
                  </xsl:attribute>

						<xsl:call-template name="Automation">
						</xsl:call-template>

					</xsl:element>

				</xsl:element>
			</xsl:when>
		</xsl:choose>
	</xsl:template>


	<xsl:template name="General">

		<xsl:element name="GeneralDetails">

			<xsl:attribute name="DialogName">
                        <xsl:value-of
				select="Standard/Control/@DialogName | MSAA/Control/@DialogName" />
                  </xsl:attribute>

			<xsl:attribute name="URL">
                        <xsl:value-of select="IE/@DocURL" />
                  </xsl:attribute>

		</xsl:element>
	</xsl:template>


	<xsl:template name="Control">
		<xsl:element name="ControlDetails">

			<xsl:attribute name="ControlName">
                        <xsl:value-of
				select="MSAA/Control/@Name" />
                  </xsl:attribute>

			<xsl:attribute name="ControlType">
                        <xsl:value-of
				select="MSAA/Control/@RoleID" />
                  </xsl:attribute>

			<xsl:attribute name="ControlData">
                        <xsl:value-of
				select="MSAA/Control/ControlData/Value" />
                  </xsl:attribute>

			<xsl:attribute name="AliasName">
                        <xsl:value-of select="@AliasName" />
                  </xsl:attribute>

			<xsl:attribute name="ShortCut">
                        <xsl:value-of
				select="MSAA/Control/@KBShortcut" />
                  </xsl:attribute>

		</xsl:element>
	</xsl:template>

	<xsl:template name="Automation">

		<xsl:element name="AutomationDetails">
		
		<xsl:attribute name="StepSentence">
                        <xsl:value-of
							select="Sentence/@Plain" />
                  </xsl:attribute>
		
		<xsl:attribute name="WindowTitle">
                        <xsl:value-of
				select="Info/Window/@Value" />
                  </xsl:attribute>
                  
                  <xsl:attribute name="ApplicationPath">
                        <xsl:value-of
				select="Info/TAP/@Path" />
                  </xsl:attribute>

			<xsl:attribute name="ExeName">
                        <xsl:value-of
				select="Info/TAP/@EXEName" />
                  </xsl:attribute>

			<xsl:attribute name="Event">
                        <xsl:value-of
				select="PlayBack/GeneralInfo/@Event" />
                  </xsl:attribute>

			<xsl:attribute name="ControlName">
                        <xsl:value-of
				select="PlayBack/ControlInfo/@ControlName" />
                  </xsl:attribute>

			<xsl:attribute name="Role">
                        <xsl:value-of
				select="PlayBack/ControlInfo/@Role" />
                  </xsl:attribute>

			<xsl:attribute name="ControlData">
                        <xsl:value-of
				select="PlayBack/ControlInfo/@ControlData" />
                  </xsl:attribute>

			<xsl:attribute name="TagName">
                        <xsl:value-of
				select="PlayBack/ControlInfo/@IEControlAttributeTagName" />
                  </xsl:attribute>

			<xsl:attribute name="ID">
                        <xsl:value-of
				select="PlayBack/ControlInfo/@UniqueID" />
                  </xsl:attribute>

			<xsl:attribute name="Title">
                        <xsl:value-of
				select="PlayBack/ControlInfo/@ScreenName" />
                  </xsl:attribute>

			<xsl:attribute name="CSSSelector">
                        <xsl:value-of
				select="PlayBack/AdditionalInfo/@Web_IFrameFullCssPath" />
                  </xsl:attribute>

			<xsl:attribute name="XPath">
                        <xsl:value-of
				select="PlayBack/ControlInfo/@XPath" />
                  </xsl:attribute>
                  
                  <xsl:attribute name="Class">
                        <xsl:value-of
				select="PlayBack__AdditionalInfo_Web_ClassName | PlayBack__ControlInfo_IEControlClassName" />
                  </xsl:attribute>

		</xsl:element>
	</xsl:template>

</xsl:stylesheet>
